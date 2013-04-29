package scenarios.third;

import static choco.Choco.*;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

/*
 * Ce scénario représente un combat entre deux guerrier (niveau 10)
 * et deux ennemis (orcs) tiré du bestiaire du jeu de rôle
 * pathfinder (un version de donjons et dragons)
 * 
 * Le guerrier à 89 points de vie et fait 46 dmg par attaque
 * 
 * Le rôdeur a 66 points de vie et fait 50 dmg par attaque
 * 
 * L'orc a 40 points de vie et fait 22 dmg par attaque
 * 
 * Le goblin a 48 points de vie et fait 15 dmg par attaque
 * 
 * Dans le cadre de ce scénario, ce sont le guerrier et le rôdeur qui agiront
 * en premier.
 * 
 * Pour garder ce scénario très simple, il n'y aura pas de mouvement
 * de la part des guerriers ni des monstres.
 * 
 * À des fins d'exploration et de simplicité, ce scénario comportera 
 * seulement 3 rounds et les contraintes et variables ne seront pas 
 * instanciées à l'aide de boucle.
 */
public class TwoWarriorVsTwoOrcsModelTwo {
  private static final int NB_OF_ROUNDS = 3;

  private static final int WARRIOR_MAX_HP = 89;
  private static final int WARRIOR_DMG = 46;
  private static final int[] WARRIOR_TARGET = { 0, 3, 4 };

  private static final int RANGER_MAX_HP = 66;
  private static final int RANGER_DMG = 50;
  private static final int[] RANGER_TARGET = { 0, 3, 4 };

  private static final int ORC_MAX_HP = 40;
  private static final int ORC_DMG = 22;
  private static final int[] ORC_TARGET = { 0, 1, 2 };

  private static final int GOBLIN_MAX_HP = 48;
  private static final int GOBLIN_DMG = 15;
  private static final int[] GOBLIN_TARGET = { 0, 1, 2 };

  private Model model;
  private Solver solveur;

  // The warrior will be identified by 1
  private IntegerVariable warriorHp1;
  private IntegerVariable warriorHp2;
  private IntegerVariable warriorHp3;
  private IntegerVariable warriorTarget1;
  private IntegerVariable warriorTarget2;
  private IntegerVariable warriorTarget3;

  // The ranger will be identified by 2
  private IntegerVariable rangerHp1;
  private IntegerVariable rangerHp2;
  private IntegerVariable rangerHp3;
  private IntegerVariable rangerTarget1;
  private IntegerVariable rangerTarget2;
  private IntegerVariable rangerTarget3;

  // The orc will be identified by 3
  private IntegerVariable orcHp1;
  private IntegerVariable orcHp2;
  private IntegerVariable orcHp3;
  private IntegerVariable orcTarget1;
  private IntegerVariable orcTarget2;
  private IntegerVariable orcTarget3;

  // The goblin will be identified by 4
  private IntegerVariable goblinHp1;
  private IntegerVariable goblinHp2;
  private IntegerVariable goblinHp3;
  private IntegerVariable goblinTarget1;
  private IntegerVariable goblinTarget2;
  private IntegerVariable goblinTarget3;

  private IntegerVariable finalPartyHp;

  public TwoWarriorVsTwoOrcsModelTwo() {
    model = new CPModel();

    prepareVariables();
    prepareRound1Constraints();
    prepareRound2Constraints();
    prepareRound3Constraints();

    finalPartyHp = makeIntVar("final party hp",
                              0,
                              WARRIOR_MAX_HP + RANGER_MAX_HP,
                              Options.V_OBJECTIVE);
    model.addVariable(finalPartyHp);
    model.addConstraint(eq(finalPartyHp, sum(warriorHp3, rangerHp3)));

  }

  private void prepareRound1Constraints() {
    // Résultat des attaques du guerrier et du rôdeur
    Constraint orcAttackedByWarrior = and(eq(warriorTarget1, 3), neq(rangerTarget1, 3));
    Constraint orcAttackedByRanger = and(neq(warriorTarget1, 3), eq(rangerTarget1, 3));
    Constraint orcAttackedByWarriorAndRanger = and(eq(warriorTarget1, 3), eq(rangerTarget1, 3));
    Constraint orcNotAttacked = and(neq(warriorTarget1, 3), neq(rangerTarget1, 3));
    model.addConstraint(implies(orcAttackedByWarrior, eq(orcHp1, ORC_MAX_HP - WARRIOR_DMG)));
    model.addConstraint(implies(orcAttackedByRanger, eq(orcHp1, ORC_MAX_HP - RANGER_DMG)));
    model.addConstraint(implies(orcAttackedByWarriorAndRanger,
                                eq(orcHp1, ORC_MAX_HP - WARRIOR_DMG - RANGER_DMG)));
    model.addConstraint(implies(orcNotAttacked, eq(orcHp1, ORC_MAX_HP)));

    Constraint goblinAttackedByWarrior = and(eq(warriorTarget1, 4), neq(rangerTarget1, 4));
    Constraint goblinAttackedByRanger = and(neq(warriorTarget1, 4), eq(rangerTarget1, 4));
    Constraint goblinAttackedByWarriorAndRanger = and(eq(warriorTarget1, 4), eq(rangerTarget1, 4));
    Constraint goblinNotAttacked = and(neq(warriorTarget1, 4), neq(rangerTarget1, 4));
    model.addConstraint(implies(goblinAttackedByWarrior, eq(goblinHp1, GOBLIN_MAX_HP - WARRIOR_DMG)));
    model.addConstraint(implies(goblinAttackedByRanger, eq(goblinHp1, GOBLIN_MAX_HP - RANGER_DMG)));
    model.addConstraint(implies(goblinAttackedByWarriorAndRanger,
                                eq(goblinHp1, GOBLIN_MAX_HP - WARRIOR_DMG - RANGER_DMG)));
    model.addConstraint(implies(goblinNotAttacked, eq(goblinHp1, GOBLIN_MAX_HP)));

    // Résultat des attaques de l'orc et du goblin
    Constraint warriorAttackedByOrc = and(eq(orcTarget1, 1), neq(goblinTarget1, 1));
    Constraint warriorAttackedByGoblin = and(neq(orcTarget1, 1), eq(goblinTarget1, 1));
    Constraint warriorAttackedByOrcAndGoblin = and(eq(orcTarget1, 1), eq(goblinTarget1, 1));
    Constraint warriorNotAttacked = and(neq(orcTarget1, 1), neq(goblinTarget1, 1));
    model.addConstraint(implies(warriorAttackedByOrc, eq(warriorHp1, WARRIOR_MAX_HP - ORC_DMG)));
    model.addConstraint(implies(warriorAttackedByGoblin,
                                eq(warriorHp1, WARRIOR_MAX_HP - GOBLIN_DMG)));
    model.addConstraint(implies(warriorAttackedByOrcAndGoblin,
                                eq(warriorHp1, WARRIOR_MAX_HP - ORC_DMG - GOBLIN_DMG)));
    model.addConstraint(implies(warriorNotAttacked, eq(warriorHp1, WARRIOR_MAX_HP)));

    Constraint rangerAttackedByOrc = and(eq(orcTarget1, 2), neq(goblinTarget1, 2));
    Constraint rangerAttackedByGoblin = and(neq(orcTarget1, 2), eq(goblinTarget1, 2));
    Constraint rangerAttackedByOrcAndGoblin = and(eq(orcTarget1, 2), eq(goblinTarget1, 2));
    Constraint rangerNotAttacked = and(neq(orcTarget1, 2), neq(goblinTarget1, 2));
    model.addConstraint(implies(rangerAttackedByOrc, eq(rangerHp1, RANGER_MAX_HP - ORC_DMG)));
    model.addConstraint(implies(rangerAttackedByGoblin, eq(rangerHp1, RANGER_MAX_HP - GOBLIN_DMG)));
    model.addConstraint(implies(rangerAttackedByOrcAndGoblin,
                                eq(rangerHp1, RANGER_MAX_HP - ORC_DMG - GOBLIN_DMG)));
    model.addConstraint(implies(rangerNotAttacked, eq(rangerHp1, RANGER_MAX_HP)));

    // Contrainte pour le choix des cibles des attaques
    // Comme le guerrier et le rôdeur attaquent avant leur
    // ennemis, la contrainte de vérification de vie n'est
    // pas nécessaire pour le round 1
    Constraint orcDead = lt(orcHp1, 0);
    Constraint goblinDead = lt(goblinHp1, 0);

    model.addConstraint(ifThenElse(orcDead, eq(orcTarget1, 0), neq(orcTarget1, 0)));
    model.addConstraint(ifThenElse(goblinDead, eq(goblinTarget1, 0), neq(goblinTarget1, 0)));

    model.addConstraint(neq(warriorTarget1, 0));

    model.addConstraint(neq(rangerTarget1, 0));
  }

  private void prepareRound2Constraints() {
    // Résultat des attaques du guerrieret du rôdeur
    Constraint orcAttackedByWarrior = and(eq(warriorTarget2, 3), neq(rangerTarget2, 3));
    Constraint orcAttackedByRanger = and(neq(warriorTarget2, 3), eq(rangerTarget2, 3));
    Constraint orcAttackedByWarriorAndRanger = and(eq(warriorTarget2, 3), eq(rangerTarget2, 3));
    Constraint orcNotAttacked = and(neq(warriorTarget2, 3), neq(rangerTarget2, 3));
    model.addConstraint(implies(orcAttackedByWarrior, eq(orcHp2, minus(orcHp1, WARRIOR_DMG))));
    model.addConstraint(implies(orcAttackedByRanger, eq(orcHp2, minus(orcHp1, RANGER_DMG))));
    model.addConstraint(implies(orcAttackedByWarriorAndRanger,
                                eq(orcHp2, minus(orcHp1, WARRIOR_DMG + RANGER_DMG))));
    model.addConstraint(implies(orcNotAttacked, eq(orcHp2, orcHp1)));

    Constraint goblinAttackedByWarrior = and(eq(warriorTarget2, 4), neq(rangerTarget2, 4));
    Constraint goblinAttackedByRanger = and(neq(warriorTarget2, 4), eq(rangerTarget2, 4));
    Constraint goblinAttackedByWarriorAndRanger = and(eq(warriorTarget2, 4), eq(rangerTarget2, 4));
    Constraint goblinNotAttacked = and(neq(warriorTarget2, 4), neq(rangerTarget2, 4));
    model.addConstraint(implies(goblinAttackedByWarrior,
                                eq(goblinHp2, minus(goblinHp1, WARRIOR_DMG))));
    model.addConstraint(implies(goblinAttackedByRanger, eq(goblinHp2, minus(goblinHp1, RANGER_DMG))));
    model.addConstraint(implies(goblinAttackedByWarriorAndRanger,
                                eq(goblinHp2, minus(goblinHp1, WARRIOR_DMG + RANGER_DMG))));
    model.addConstraint(implies(goblinNotAttacked, eq(goblinHp2, goblinHp1)));

    // Résultat des attaques de l'orc et du goblin
    Constraint warriorAttackedByOrc = and(eq(orcTarget2, 1), neq(goblinTarget2, 1));
    Constraint warriorAttackedByGoblin = and(neq(orcTarget2, 1), eq(goblinTarget2, 1));
    Constraint warriorAttackedByOrcAndGoblin = and(eq(orcTarget2, 1), eq(goblinTarget2, 1));
    Constraint warriorNotAttacked = and(neq(orcTarget2, 1), neq(goblinTarget2, 1));
    model.addConstraint(implies(warriorAttackedByOrc, eq(warriorHp2, minus(warriorHp1, ORC_DMG))));
    model.addConstraint(implies(warriorAttackedByGoblin,
                                eq(warriorHp2, minus(warriorHp1, GOBLIN_DMG))));
    model.addConstraint(implies(warriorAttackedByOrcAndGoblin,
                                eq(warriorHp2, minus(warriorHp1, ORC_DMG + GOBLIN_DMG))));
    model.addConstraint(implies(warriorNotAttacked, eq(warriorHp2, warriorHp1)));

    Constraint rangerAttackedByOrc = and(eq(orcTarget2, 2), neq(goblinTarget2, 2));
    Constraint rangerAttackedByGoblin = and(neq(orcTarget2, 2), eq(goblinTarget2, 2));
    Constraint rangerAttackedByOrcAndGoblin = and(eq(orcTarget2, 2), eq(goblinTarget2, 2));
    Constraint rangerNotAttacked = and(neq(orcTarget2, 2), neq(goblinTarget2, 2));
    model.addConstraint(implies(rangerAttackedByOrc, eq(rangerHp2, minus(rangerHp1, ORC_DMG))));
    model.addConstraint(implies(rangerAttackedByGoblin, eq(rangerHp2, minus(rangerHp1, GOBLIN_DMG))));
    model.addConstraint(implies(rangerAttackedByOrcAndGoblin,
                                eq(rangerHp2, minus(rangerHp1, ORC_DMG + GOBLIN_DMG))));
    model.addConstraint(implies(rangerNotAttacked, eq(rangerHp2, rangerHp1)));

    // Contrainte pour le choix des cibles des attaques
    Constraint orcDead = lt(orcHp2, 0);
    Constraint goblinDead = lt(goblinHp2, 0);
    Constraint warriorDead = lt(warriorHp2, 0);
    Constraint rangerDead = lt(rangerHp2, 0);

    model.addConstraint(ifThenElse(orcDead, eq(orcTarget2, 0), neq(orcTarget2, 0)));
    model.addConstraint(implies(warriorDead, neq(orcTarget2, 1)));
    model.addConstraint(implies(rangerDead, neq(orcTarget2, 2)));

    model.addConstraint(ifThenElse(goblinDead, eq(goblinTarget2, 0), neq(goblinTarget2, 0)));
    model.addConstraint(implies(warriorDead, neq(goblinTarget2, 1)));
    model.addConstraint(implies(rangerDead, neq(goblinTarget2, 2)));

    model.addConstraint(ifThenElse(warriorDead, eq(warriorTarget2, 0), neq(warriorTarget2, 0)));
    model.addConstraint(implies(warriorDead, eq(warriorTarget2, 0)));

    model.addConstraint(ifThenElse(rangerDead, eq(rangerTarget2, 0), neq(rangerTarget2, 0)));
    model.addConstraint(implies(rangerDead, eq(rangerTarget2, 0)));
  }

  private void prepareRound3Constraints() {
    // Résultat des attaques du guerrier et du rôdeur
    Constraint orcAttackedByWarrior = and(eq(warriorTarget3, 3), neq(rangerTarget3, 3));
    Constraint orcAttackedByRanger = and(neq(warriorTarget3, 3), eq(rangerTarget3, 3));
    Constraint orcAttackedByWarriorAndRanger = and(eq(warriorTarget3, 3), eq(rangerTarget3, 3));
    Constraint orcNotAttacked = and(neq(warriorTarget3, 3), neq(rangerTarget3, 3));
    model.addConstraint(implies(orcAttackedByWarrior, eq(orcHp3, minus(orcHp2, WARRIOR_DMG))));
    model.addConstraint(implies(orcAttackedByRanger, eq(orcHp3, minus(orcHp2, RANGER_DMG))));
    model.addConstraint(implies(orcAttackedByWarriorAndRanger,
                                eq(orcHp3, minus(orcHp2, WARRIOR_DMG + RANGER_DMG))));
    model.addConstraint(implies(orcNotAttacked, eq(orcHp3, orcHp2)));

    Constraint goblinAttackedByWarrior = and(eq(warriorTarget3, 4), neq(rangerTarget3, 4));
    Constraint goblinAttackedByRanger = and(neq(warriorTarget3, 4), eq(rangerTarget3, 4));
    Constraint goblinAttackedByWarriorAndRanger = and(eq(warriorTarget3, 4), eq(rangerTarget3, 4));
    Constraint goblinNotAttacked = and(neq(warriorTarget3, 4), neq(rangerTarget3, 4));
    model.addConstraint(implies(goblinAttackedByWarrior,
                                eq(goblinHp3, minus(goblinHp2, WARRIOR_DMG))));
    model.addConstraint(implies(goblinAttackedByRanger, eq(goblinHp3, minus(goblinHp2, RANGER_DMG))));
    model.addConstraint(implies(goblinAttackedByWarriorAndRanger,
                                eq(goblinHp3, minus(goblinHp2, WARRIOR_DMG + RANGER_DMG))));
    model.addConstraint(implies(goblinNotAttacked, eq(goblinHp3, goblinHp2)));

    // Résultat des attaques de l'orc et du goblin
    Constraint warriorAttackedByOrc = and(eq(orcTarget3, 1), neq(goblinTarget3, 1));
    Constraint warriorAttackedByGoblin = and(neq(orcTarget3, 1), eq(goblinTarget3, 1));
    Constraint warriorAttackedByOrcAndGoblin = and(eq(orcTarget3, 1), eq(goblinTarget3, 1));
    Constraint warriorNotAttacked = and(neq(orcTarget3, 1), neq(goblinTarget3, 1));
    model.addConstraint(implies(warriorAttackedByOrc, eq(warriorHp3, minus(warriorHp2, ORC_DMG))));
    model.addConstraint(implies(warriorAttackedByGoblin,
                                eq(warriorHp3, minus(warriorHp2, GOBLIN_DMG))));
    model.addConstraint(implies(warriorAttackedByOrcAndGoblin,
                                eq(warriorHp3, minus(warriorHp2, ORC_DMG + GOBLIN_DMG))));
    model.addConstraint(implies(warriorNotAttacked, eq(warriorHp3, warriorHp2)));

    Constraint rangerAttackedByOrc = and(eq(orcTarget3, 2), neq(goblinTarget3, 2));
    Constraint rangerAttackedByGoblin = and(neq(orcTarget3, 2), eq(goblinTarget3, 2));
    Constraint rangerAttackedByOrcAndGoblin = and(eq(orcTarget3, 2), eq(goblinTarget3, 2));
    Constraint rangerNotAttacked = and(neq(orcTarget3, 2), neq(goblinTarget3, 2));
    model.addConstraint(implies(rangerAttackedByOrc, eq(rangerHp3, minus(rangerHp2, ORC_DMG))));
    model.addConstraint(implies(rangerAttackedByGoblin, eq(rangerHp3, minus(rangerHp2, GOBLIN_DMG))));
    model.addConstraint(implies(rangerAttackedByOrcAndGoblin,
                                eq(rangerHp3, minus(rangerHp2, ORC_DMG + GOBLIN_DMG))));
    model.addConstraint(implies(rangerNotAttacked, eq(rangerHp3, rangerHp2)));

    // Contrainte pour le choix des cibles des attaques
    Constraint orcDead = lt(orcHp3, 0);
    Constraint goblinDead = lt(goblinHp3, 0);
    Constraint warriorDead = lt(warriorHp3, 0);
    Constraint rangerDead = lt(rangerHp3, 0);

    model.addConstraint(ifThenElse(orcDead, eq(orcTarget3, 0), neq(orcTarget3, 0)));
    model.addConstraint(implies(warriorDead, neq(goblinTarget3, 1)));
    model.addConstraint(implies(rangerDead, neq(goblinTarget3, 2)));

    model.addConstraint(ifThenElse(goblinDead, eq(goblinTarget3, 0), neq(goblinTarget3, 0)));
    model.addConstraint(implies(warriorDead, neq(goblinTarget3, 1)));
    model.addConstraint(implies(rangerDead, neq(goblinTarget3, 2)));

    model.addConstraint(ifThenElse(warriorDead, eq(warriorTarget3, 0), neq(warriorTarget3, 0)));
    model.addConstraint(implies(warriorDead, eq(warriorTarget3, 0)));

    model.addConstraint(ifThenElse(rangerDead, eq(rangerTarget3, 0), neq(rangerTarget3, 0)));
    model.addConstraint(implies(rangerDead, eq(rangerTarget3, 0)));
  }

  public void solveScenario() {
    solveur = new CPSolver();
    // Lecture du modele par le solveur
    solveur.read(model);
    solveur.setVarIntSelector(new StaticVarOrder(solveur, solveur.getVar(prepareHeuristic())));
    if (solveur.maximize(false)) {
      System.out.println("Warrior final hp = " + solveur.getVar(warriorHp3).getVal());
      System.out.println("Ranger final hp = " + solveur.getVar(rangerHp3).getVal());
      System.out.println("----------------------------");
      System.out.println("Orc final hp = " + solveur.getVar(orcHp3).getVal());
      System.out.println("Goblin final hp = " + solveur.getVar(goblinHp3).getVal());
      System.out.println("----------------------------");
      System.out.println("ROUND 1");
      System.out.println("Warrior target = " + solveur.getVar(warriorTarget1).getVal());
      System.out.println("Ranger target = " + solveur.getVar(rangerTarget1).getVal());
      System.out.println("Orc target = " + solveur.getVar(orcTarget1).getVal());
      System.out.println("Goblin target = " + solveur.getVar(goblinTarget1).getVal());
      System.out.println("----------------------------");
      System.out.println("Warrior hp after round 1 = " + solveur.getVar(warriorHp1).getVal());
      System.out.println("Ranger hp after round 1 = " + solveur.getVar(rangerHp1).getVal());
      System.out.println("Orc hp after round 1 = " + solveur.getVar(orcHp1).getVal());
      System.out.println("Goblin hp after round 1 = " + solveur.getVar(goblinHp1).getVal());
      System.out.println("----------------------------");
      System.out.println("ROUND 2");
      System.out.println("Warrior target = " + solveur.getVar(warriorTarget2).getVal());
      System.out.println("Ranger target = " + solveur.getVar(rangerTarget2).getVal());
      System.out.println("Orc target = " + solveur.getVar(orcTarget2).getVal());
      System.out.println("Goblin target = " + solveur.getVar(goblinTarget2).getVal());
      System.out.println("----------------------------");
      System.out.println("Warrior hp after round 2 = " + solveur.getVar(warriorHp2).getVal());
      System.out.println("Ranger hp after round 2 = " + solveur.getVar(rangerHp2).getVal());
      System.out.println("Orc hp after round 2 = " + solveur.getVar(orcHp2).getVal());
      System.out.println("Goblin hp after round 2 = " + solveur.getVar(goblinHp2).getVal());
      System.out.println("----------------------------");
      System.out.println("ROUND 3");
      System.out.println("Warrior target = " + solveur.getVar(warriorTarget3).getVal());
      System.out.println("Ranger target = " + solveur.getVar(rangerTarget3).getVal());
      System.out.println("Orc target = " + solveur.getVar(orcTarget3).getVal());
      System.out.println("Goblin target = " + solveur.getVar(goblinTarget3).getVal());
      System.out.println("----------------------------");
      System.out.println("Warrior hp after round 3 = " + solveur.getVar(warriorHp3).getVal());
      System.out.println("Ranger hp after round 3 = " + solveur.getVar(rangerHp3).getVal());
      System.out.println("Orc hp after round 3 = " + solveur.getVar(orcHp3).getVal());
      System.out.println("Goblin hp after round 3 = " + solveur.getVar(goblinHp3).getVal());
      System.out.println("----------------------------");
      System.out.println("Total party life = " + solveur.getVar(finalPartyHp).getVal());
    } else {
      System.out.println("Aucune solution trouvee.");
    }
    System.out.println("--------------------------------");
    System.out.println("Probleme resolu en " + solveur.getTimeCount() + " millisecondes.");
    System.out.println("Probleme resolu avec " + solveur.getBackTrackCount() + " retours arrieres.");
  }

  private void prepareVariables() {
    warriorHp1 = makeIntVar("warrior hp", 52, WARRIOR_MAX_HP);
    warriorHp2 = makeIntVar("warrior hp", 15, WARRIOR_MAX_HP);
    warriorHp3 = makeIntVar("warrior hp", -22, WARRIOR_MAX_HP);
    warriorTarget1 = makeIntVar("warrior target 1", WARRIOR_TARGET);
    warriorTarget2 = makeIntVar("warrior target 2", WARRIOR_TARGET);
    warriorTarget3 = makeIntVar("warrior target 3", WARRIOR_TARGET);
    model.addVariable(warriorHp1);
    model.addVariable(warriorHp2);
    model.addVariable(warriorHp3);
    model.addVariable(warriorTarget1);
    model.addVariable(warriorTarget2);
    model.addVariable(warriorTarget3);

    rangerHp1 = makeIntVar("ranger hp", 29, RANGER_MAX_HP);
    rangerHp2 = makeIntVar("ranger hp", -8, RANGER_MAX_HP);
    rangerHp3 = makeIntVar("ranger hp", -45, RANGER_MAX_HP);
    rangerTarget1 = makeIntVar("ranger target 1", RANGER_TARGET);
    rangerTarget2 = makeIntVar("ranger target 2", RANGER_TARGET);
    rangerTarget3 = makeIntVar("ranger target 3", RANGER_TARGET);
    model.addVariable(rangerHp1);
    model.addVariable(rangerHp2);
    model.addVariable(rangerHp3);
    model.addVariable(rangerTarget1);
    model.addVariable(rangerTarget2);
    model.addVariable(rangerTarget3);

    orcHp1 = makeIntVar("orc hp", -38, ORC_MAX_HP);
    orcHp2 = makeIntVar("orc hp", -116, ORC_MAX_HP);
    orcHp3 = makeIntVar("orc hp", -194, ORC_MAX_HP);
    orcTarget1 = makeIntVar("orc target 1", ORC_TARGET);
    orcTarget2 = makeIntVar("orc target 2", ORC_TARGET);
    orcTarget3 = makeIntVar("orc target 3", ORC_TARGET);
    model.addVariable(orcHp1);
    model.addVariable(orcHp2);
    model.addVariable(orcHp3);
    model.addVariable(orcTarget1);
    model.addVariable(orcTarget2);
    model.addVariable(orcTarget3);

    goblinHp1 = makeIntVar("goblin hp", -30, GOBLIN_MAX_HP);
    goblinHp2 = makeIntVar("goblin hp", -108, GOBLIN_MAX_HP);
    goblinHp3 = makeIntVar("goblin hp", -186, GOBLIN_MAX_HP);
    goblinTarget1 = makeIntVar("goblin target 1", GOBLIN_TARGET);
    goblinTarget2 = makeIntVar("goblin target 2", GOBLIN_TARGET);
    goblinTarget3 = makeIntVar("goblin target 3", GOBLIN_TARGET);
    model.addVariable(goblinHp1);
    model.addVariable(goblinHp2);
    model.addVariable(goblinHp3);
    model.addVariable(goblinTarget1);
    model.addVariable(goblinTarget2);
    model.addVariable(goblinTarget3);
  }

  private IntegerVariable[] prepareHeuristic() {
    IntegerVariable[] instantiationOrder = new IntegerVariable[24];
    instantiationOrder[0] = warriorTarget1;
    instantiationOrder[1] = rangerTarget1;
    instantiationOrder[2] = orcHp1;
    instantiationOrder[3] = goblinHp1;
    instantiationOrder[4] = orcTarget1;
    instantiationOrder[5] = goblinTarget1;
    instantiationOrder[6] = warriorHp1;
    instantiationOrder[7] = rangerHp1;
    instantiationOrder[8] = warriorTarget2;
    instantiationOrder[9] = rangerTarget2;
    instantiationOrder[10] = orcHp2;
    instantiationOrder[11] = goblinHp2;
    instantiationOrder[12] = orcTarget2;
    instantiationOrder[13] = goblinTarget2;
    instantiationOrder[14] = warriorHp2;
    instantiationOrder[15] = rangerHp2;
    instantiationOrder[16] = warriorTarget3;
    instantiationOrder[17] = rangerTarget3;
    instantiationOrder[18] = orcHp3;
    instantiationOrder[19] = goblinHp3;
    instantiationOrder[20] = orcTarget3;
    instantiationOrder[21] = goblinTarget3;
    instantiationOrder[22] = warriorHp3;
    instantiationOrder[23] = rangerHp3;

    return instantiationOrder;

  }

}
