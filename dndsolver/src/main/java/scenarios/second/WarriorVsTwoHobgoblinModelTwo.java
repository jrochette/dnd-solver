package scenarios.second;

import static choco.Choco.*;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

public class WarriorVsTwoHobgoblinModelTwo {
  private static final int NB_OF_ROUNDS = 5;

  private static final int WARRIOR_MAX_HP = 89;
  private static final int WARRIOR_DMG = 46;
  private static final int[] WARRIOR_TARGET = { 0, 2, 3 };

  private static final int ORC_MAX_HP = 48;
  private static final int ORC_DMG = 15;
  private static final int[] ORC_TARGET = { 0, 1 };

  private static final int GOBLIN_MAX_HP = 48;
  private static final int GOBLIN_DMG = 12;
  private static final int[] GOBLIN_TARGET = { 0, 1 };

  private Model model;
  private Solver solveur;

  // The warrior will be identified by 1
  private IntegerVariable warriorHp1;
  private IntegerVariable warriorHp2;
  private IntegerVariable warriorHp3;
  private IntegerVariable warriorHp4;
  private IntegerVariable warriorHp5;
  private IntegerVariable warriorTarget1;
  private IntegerVariable warriorTarget2;
  private IntegerVariable warriorTarget3;
  private IntegerVariable warriorTarget4;
  private IntegerVariable warriorTarget5;

  // The orc will be identified by 3
  private IntegerVariable orcHp1;
  private IntegerVariable orcHp2;
  private IntegerVariable orcHp3;
  private IntegerVariable orcHp4;
  private IntegerVariable orcHp5;
  private IntegerVariable orcTarget1;
  private IntegerVariable orcTarget2;
  private IntegerVariable orcTarget3;
  private IntegerVariable orcTarget4;
  private IntegerVariable orcTarget5;

  // The goblin will be identified by 4
  private IntegerVariable goblinHp1;
  private IntegerVariable goblinHp2;
  private IntegerVariable goblinHp3;
  private IntegerVariable goblinHp4;
  private IntegerVariable goblinHp5;
  private IntegerVariable goblinTarget1;
  private IntegerVariable goblinTarget2;
  private IntegerVariable goblinTarget3;
  private IntegerVariable goblinTarget4;
  private IntegerVariable goblinTarget5;

  private IntegerVariable finalPartyHp;

  public WarriorVsTwoHobgoblinModelTwo() {
    model = new CPModel();

    prepareVariables();
    prepareRound1Constraints();
    prepareRound2Constraints();
    prepareRound3Constraints();
    prepareRound4Constraints();
    prepareRound5Constraints();

    finalPartyHp = makeIntVar("final party hp", 0, WARRIOR_MAX_HP, Options.V_OBJECTIVE);
    model.addVariable(finalPartyHp);
    model.addConstraint(eq(finalPartyHp, warriorHp5));

  }

  private void prepareRound1Constraints() {
    // Résultat des attaques de warrior et ranger
    Constraint orcAttackedByWarrior = eq(warriorTarget1, 2);
    Constraint orcNotAttacked = neq(warriorTarget1, 2);
    model.addConstraint(implies(orcAttackedByWarrior, eq(orcHp1, ORC_MAX_HP - WARRIOR_DMG)));
    model.addConstraint(implies(orcNotAttacked, eq(orcHp1, ORC_MAX_HP)));

    Constraint goblinAttackedByWarrior = eq(warriorTarget1, 3);
    Constraint goblinNotAttacked = neq(warriorTarget1, 3);
    model.addConstraint(implies(goblinAttackedByWarrior, eq(goblinHp1, GOBLIN_MAX_HP - WARRIOR_DMG)));
    model.addConstraint(implies(goblinNotAttacked, eq(goblinHp1, GOBLIN_MAX_HP)));

    // Résultat des attaques de orc et goblin
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

    // Contrainte pour le choix des cibles des attaques
    // Comme le guerrier et le ranger attaquent avant leur
    // ennemis, la contrainte de vérification de vie n'est
    // pas nécessaire pour le round 1
    Constraint orcDead = lt(orcHp1, 0);
    Constraint goblinDead = lt(goblinHp1, 0);

    model.addConstraint(ifThenElse(orcDead, eq(orcTarget1, 0), neq(orcTarget1, 0)));
    model.addConstraint(ifThenElse(goblinDead, eq(goblinTarget1, 0), neq(goblinTarget1, 0)));

    model.addConstraint(neq(warriorTarget1, 0));
    // model.addConstraint(implies(goblinDead, neq(warriorTarget1, 4)));
    // model.addConstraint(implies(orcDead, neq(warriorTarget1, 3)));
  }

  private void prepareRound2Constraints() {
    // Résultat des attaques de warrior et ranger
    Constraint orcAttackedByWarrior = eq(warriorTarget2, 2);
    Constraint orcNotAttacked = neq(warriorTarget2, 2);
    model.addConstraint(implies(orcAttackedByWarrior, eq(orcHp2, minus(orcHp1, WARRIOR_DMG))));
    model.addConstraint(implies(orcNotAttacked, eq(orcHp2, orcHp1)));

    Constraint goblinAttackedByWarrior = eq(warriorTarget1, 3);
    Constraint goblinNotAttacked = neq(warriorTarget1, 3);
    model.addConstraint(implies(goblinAttackedByWarrior,
                                eq(goblinHp2, minus(goblinHp2, WARRIOR_DMG))));
    model.addConstraint(implies(goblinNotAttacked, eq(goblinHp2, goblinHp1)));

    // Résultat des attaques de orc et goblin
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

    // Contrainte pour le choix des cibles des attaques
    Constraint orcDead = lt(orcHp2, 0);
    Constraint goblinDead = lt(goblinHp2, 0);
    Constraint warriorDead = lt(warriorHp2, 0);

    model.addConstraint(ifThenElse(orcDead, eq(orcTarget2, 0), neq(orcTarget2, 0)));
    // model.addConstraint(implies(warriorDead, neq(orcTarget2, 1)));

    model.addConstraint(ifThenElse(goblinDead, eq(goblinTarget2, 0), neq(goblinTarget2, 0)));
    // model.addConstraint(implies(warriorDead, neq(goblinTarget2, 1)));

    model.addConstraint(ifThenElse(warriorDead, eq(warriorTarget2, 0), neq(warriorTarget2, 0)));
    model.addConstraint(implies(warriorDead, eq(warriorTarget2, 0)));
    // model.addConstraint(implies(goblinDead, neq(warriorTarget2, 4)));
    // model.addConstraint(implies(orcDead, neq(warriorTarget2, 3)));
  }

  private void prepareRound3Constraints() {
    // Résultat des attaques de warrior et ranger
    Constraint orcAttackedByWarrior = eq(warriorTarget3, 2);
    Constraint orcNotAttacked = neq(warriorTarget3, 2);
    model.addConstraint(implies(orcAttackedByWarrior, eq(orcHp3, minus(orcHp2, WARRIOR_DMG))));
    model.addConstraint(implies(orcNotAttacked, eq(orcHp3, orcHp2)));

    Constraint goblinAttackedByWarrior = eq(warriorTarget3, 3);
    Constraint goblinNotAttacked = neq(warriorTarget3, 3);
    model.addConstraint(implies(goblinAttackedByWarrior,
                                eq(goblinHp3, minus(goblinHp2, WARRIOR_DMG))));
    model.addConstraint(implies(goblinNotAttacked, eq(goblinHp3, goblinHp2)));

    // Résultat des attaques de orc et goblin
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

    // Contrainte pour le choix des cibles des attaques
    Constraint orcDead = lt(orcHp3, 0);
    Constraint goblinDead = lt(goblinHp3, 0);
    Constraint warriorDead = lt(warriorHp3, 0);

    model.addConstraint(ifThenElse(orcDead, eq(orcTarget3, 0), neq(orcTarget3, 0)));
    // model.addConstraint(implies(warriorDead, neq(goblinTarget3, 1)));

    model.addConstraint(ifThenElse(goblinDead, eq(goblinTarget3, 0), neq(goblinTarget3, 0)));
    // model.addConstraint(implies(warriorDead, neq(goblinTarget3, 1)));

    model.addConstraint(ifThenElse(warriorDead, eq(warriorTarget3, 0), neq(warriorTarget3, 0)));
    model.addConstraint(implies(warriorDead, eq(warriorTarget3, 0)));
    // model.addConstraint(implies(goblinDead, neq(warriorTarget3, 4)));
    // model.addConstraint(implies(orcDead, neq(warriorTarget3, 3)));
  }

  private void prepareRound4Constraints() {
    // Résultat des attaques de warrior et ranger
    Constraint orcAttackedByWarrior = eq(warriorTarget4, 2);
    Constraint orcNotAttacked = neq(warriorTarget4, 2);
    model.addConstraint(implies(orcAttackedByWarrior, eq(orcHp4, minus(orcHp3, WARRIOR_DMG))));
    model.addConstraint(implies(orcNotAttacked, eq(orcHp4, orcHp3)));

    Constraint goblinAttackedByWarrior = eq(warriorTarget4, 3);
    Constraint goblinNotAttacked = neq(warriorTarget4, 3);
    model.addConstraint(implies(goblinAttackedByWarrior,
                                eq(goblinHp4, minus(goblinHp3, WARRIOR_DMG))));
    model.addConstraint(implies(goblinNotAttacked, eq(goblinHp4, goblinHp3)));

    // Résultat des attaques de orc et goblin
    Constraint warriorAttackedByOrc = and(eq(orcTarget4, 1), neq(goblinTarget4, 1));
    Constraint warriorAttackedByGoblin = and(neq(orcTarget4, 1), eq(goblinTarget4, 1));
    Constraint warriorAttackedByOrcAndGoblin = and(eq(orcTarget4, 1), eq(goblinTarget4, 1));
    Constraint warriorNotAttacked = and(neq(orcTarget4, 1), neq(goblinTarget4, 1));
    model.addConstraint(implies(warriorAttackedByOrc, eq(warriorHp4, minus(warriorHp3, ORC_DMG))));
    model.addConstraint(implies(warriorAttackedByGoblin,
                                eq(warriorHp4, minus(warriorHp3, GOBLIN_DMG))));
    model.addConstraint(implies(warriorAttackedByOrcAndGoblin,
                                eq(warriorHp4, minus(warriorHp3, ORC_DMG + GOBLIN_DMG))));
    model.addConstraint(implies(warriorNotAttacked, eq(warriorHp4, warriorHp3)));

    // Contrainte pour le choix des cibles des attaques
    Constraint orcDead = lt(orcHp4, 0);
    Constraint goblinDead = lt(goblinHp4, 0);
    Constraint warriorDead = lt(warriorHp4, 0);

    model.addConstraint(ifThenElse(orcDead, eq(orcTarget4, 0), neq(orcTarget4, 0)));
    // model.addConstraint(implies(warriorDead, neq(goblinTarget3, 1)));

    model.addConstraint(ifThenElse(goblinDead, eq(goblinTarget4, 0), neq(goblinTarget4, 0)));
    // model.addConstraint(implies(warriorDead, neq(goblinTarget3, 1)));

    model.addConstraint(ifThenElse(warriorDead, eq(warriorTarget4, 0), neq(warriorTarget4, 0)));
    model.addConstraint(implies(warriorDead, eq(warriorTarget4, 0)));
    // model.addConstraint(implies(goblinDead, neq(warriorTarget3, 4)));
    // model.addConstraint(implies(orcDead, neq(warriorTarget3, 3)));
  }

  private void prepareRound5Constraints() {
    // Résultat des attaques de warrior et ranger
    Constraint orcAttackedByWarrior = eq(warriorTarget5, 2);
    Constraint orcNotAttacked = neq(warriorTarget5, 2);
    model.addConstraint(implies(orcAttackedByWarrior, eq(orcHp5, minus(orcHp4, WARRIOR_DMG))));
    model.addConstraint(implies(orcNotAttacked, eq(orcHp5, orcHp4)));

    Constraint goblinAttackedByWarrior = eq(warriorTarget5, 3);
    Constraint goblinNotAttacked = neq(warriorTarget5, 3);
    model.addConstraint(implies(goblinAttackedByWarrior,
                                eq(goblinHp5, minus(goblinHp4, WARRIOR_DMG))));
    model.addConstraint(implies(goblinNotAttacked, eq(goblinHp5, goblinHp4)));

    // Résultat des attaques de orc et goblin
    Constraint warriorAttackedByOrc = and(eq(orcTarget5, 1), neq(goblinTarget5, 1));
    Constraint warriorAttackedByGoblin = and(neq(orcTarget5, 1), eq(goblinTarget5, 1));
    Constraint warriorAttackedByOrcAndGoblin = and(eq(orcTarget5, 1), eq(goblinTarget5, 1));
    Constraint warriorNotAttacked = and(neq(orcTarget5, 1), neq(goblinTarget5, 1));
    model.addConstraint(implies(warriorAttackedByOrc, eq(warriorHp5, minus(warriorHp3, ORC_DMG))));
    model.addConstraint(implies(warriorAttackedByGoblin,
                                eq(warriorHp5, minus(warriorHp4, GOBLIN_DMG))));
    model.addConstraint(implies(warriorAttackedByOrcAndGoblin,
                                eq(warriorHp5, minus(warriorHp4, ORC_DMG + GOBLIN_DMG))));
    model.addConstraint(implies(warriorNotAttacked, eq(warriorHp5, warriorHp4)));

    // Contrainte pour le choix des cibles des attaques
    Constraint orcDead = lt(orcHp5, 0);
    Constraint goblinDead = lt(goblinHp5, 0);
    Constraint warriorDead = lt(warriorHp5, 0);

    model.addConstraint(ifThenElse(orcDead, eq(orcTarget5, 0), neq(orcTarget5, 0)));
    // model.addConstraint(implies(warriorDead, neq(goblinTarget3, 1)));

    model.addConstraint(ifThenElse(goblinDead, eq(goblinTarget5, 0), neq(goblinTarget5, 0)));
    // model.addConstraint(implies(warriorDead, neq(goblinTarget3, 1)));

    model.addConstraint(ifThenElse(warriorDead, eq(warriorTarget5, 0), neq(warriorTarget5, 0)));
    model.addConstraint(implies(warriorDead, eq(warriorTarget5, 0)));
    // model.addConstraint(implies(goblinDead, neq(warriorTarget3, 4)));
    // model.addConstraint(implies(orcDead, neq(warriorTarget3, 3)));
  }

  public void solveScenario() {
    solveur = new CPSolver();
    // Lecture du modele par le solveur
    solveur.read(model);
    solveur.setVarIntSelector(new StaticVarOrder(solveur, solveur.getVar(prepareHeuristic())));
    if (solveur.maximize(false)) {
      System.out.println("Warrior final hp = " + solveur.getVar(warriorHp5).getVal());
      System.out.println("----------------------------");
      System.out.println("Orc final hp = " + solveur.getVar(orcHp5).getVal());
      System.out.println("Goblin final hp = " + solveur.getVar(goblinHp5).getVal());
      System.out.println("----------------------------");
      System.out.println("ROUND 1");
      System.out.println("Warrior target = " + solveur.getVar(warriorTarget1).getVal());
      System.out.println("Orc target = " + solveur.getVar(orcTarget1).getVal());
      System.out.println("Goblin target = " + solveur.getVar(goblinTarget1).getVal());
      System.out.println("----------------------------");
      System.out.println("Warrior hp after round 1 = " + solveur.getVar(warriorHp1).getVal());
      System.out.println("Orc hp after round 1 = " + solveur.getVar(orcHp1).getVal());
      System.out.println("Goblin hp after round 1 = " + solveur.getVar(goblinHp1).getVal());
      System.out.println("----------------------------");
      System.out.println("ROUND 2");
      System.out.println("Warrior target = " + solveur.getVar(warriorTarget2).getVal());
      System.out.println("Orc target = " + solveur.getVar(orcTarget2).getVal());
      System.out.println("Goblin target = " + solveur.getVar(goblinTarget2).getVal());
      System.out.println("----------------------------");
      System.out.println("Warrior hp after round 2 = " + solveur.getVar(warriorHp2).getVal());
      System.out.println("Orc hp after round 2 = " + solveur.getVar(orcHp2).getVal());
      System.out.println("Goblin hp after round 2 = " + solveur.getVar(goblinHp2).getVal());
      System.out.println("----------------------------");
      System.out.println("ROUND 3");
      System.out.println("Warrior target = " + solveur.getVar(warriorTarget3).getVal());
      System.out.println("Orc target = " + solveur.getVar(orcTarget3).getVal());
      System.out.println("Goblin target = " + solveur.getVar(goblinTarget3).getVal());
      System.out.println("----------------------------");
      System.out.println("Warrior hp after round 3 = " + solveur.getVar(warriorHp3).getVal());
      System.out.println("Orc hp after round 3 = " + solveur.getVar(orcHp3).getVal());
      System.out.println("Goblin hp after round 3 = " + solveur.getVar(goblinHp3).getVal());
      System.out.println("----------------------------");
      System.out.println("ROUND 4");
      System.out.println("Warrior target = " + solveur.getVar(warriorTarget4).getVal());
      System.out.println("Orc target = " + solveur.getVar(orcTarget4).getVal());
      System.out.println("Goblin target = " + solveur.getVar(goblinTarget4).getVal());
      System.out.println("----------------------------");
      System.out.println("Warrior hp after round 4 = " + solveur.getVar(warriorHp4).getVal());
      System.out.println("Orc hp after round 4 = " + solveur.getVar(orcHp4).getVal());
      System.out.println("Goblin hp after round 4 = " + solveur.getVar(goblinHp4).getVal());
      System.out.println("----------------------------");
      System.out.println("ROUND 5");
      System.out.println("Warrior target = " + solveur.getVar(warriorTarget5).getVal());
      System.out.println("Orc target = " + solveur.getVar(orcTarget5).getVal());
      System.out.println("Goblin target = " + solveur.getVar(goblinTarget5).getVal());
      System.out.println("----------------------------");
      System.out.println("Warrior hp after round 5 = " + solveur.getVar(warriorHp5).getVal());
      System.out.println("Orc hp after round 5 = " + solveur.getVar(orcHp5).getVal());
      System.out.println("Goblin hp after round 5 = " + solveur.getVar(goblinHp5).getVal());
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
    warriorHp4 = makeIntVar("warrior hp", -49, WARRIOR_MAX_HP);
    warriorHp5 = makeIntVar("warrior hp", -76, WARRIOR_MAX_HP);
    warriorTarget1 = makeIntVar("warrior target 1", WARRIOR_TARGET);
    warriorTarget2 = makeIntVar("warrior target 2", WARRIOR_TARGET);
    warriorTarget3 = makeIntVar("warrior target 3", WARRIOR_TARGET);
    warriorTarget4 = makeIntVar("warrior target 4", WARRIOR_TARGET);
    warriorTarget5 = makeIntVar("warrior target 5", WARRIOR_TARGET);
    model.addVariable(warriorHp1);
    model.addVariable(warriorHp2);
    model.addVariable(warriorHp3);
    model.addVariable(warriorHp4);
    model.addVariable(warriorHp5);
    model.addVariable(warriorTarget1);
    model.addVariable(warriorTarget2);
    model.addVariable(warriorTarget3);
    model.addVariable(warriorTarget4);
    model.addVariable(warriorTarget5);

    orcHp1 = makeIntVar("orc hp", 2, ORC_MAX_HP);
    orcHp2 = makeIntVar("orc hp", -44, ORC_MAX_HP);
    orcHp3 = makeIntVar("orc hp", -90, ORC_MAX_HP);
    orcHp4 = makeIntVar("orc hp", -136, ORC_MAX_HP);
    orcHp5 = makeIntVar("orc hp", -172, ORC_MAX_HP);
    orcTarget1 = makeIntVar("orc target 1", ORC_TARGET);
    orcTarget2 = makeIntVar("orc target 2", ORC_TARGET);
    orcTarget3 = makeIntVar("orc target 3", ORC_TARGET);
    orcTarget4 = makeIntVar("orc target 4", ORC_TARGET);
    orcTarget5 = makeIntVar("orc target 5", ORC_TARGET);
    model.addVariable(orcHp1);
    model.addVariable(orcHp2);
    model.addVariable(orcHp3);
    model.addVariable(orcHp4);
    model.addVariable(orcHp5);
    model.addVariable(orcTarget1);
    model.addVariable(orcTarget2);
    model.addVariable(orcTarget3);
    model.addVariable(orcTarget4);
    model.addVariable(orcTarget5);

    goblinHp1 = makeIntVar("goblin hp", 2, GOBLIN_MAX_HP);
    goblinHp2 = makeIntVar("goblin hp", -44, GOBLIN_MAX_HP);
    goblinHp3 = makeIntVar("goblin hp", -90, GOBLIN_MAX_HP);
    goblinHp4 = makeIntVar("goblin hp", -136, GOBLIN_MAX_HP);
    goblinHp5 = makeIntVar("goblin hp", -172, GOBLIN_MAX_HP);
    goblinTarget1 = makeIntVar("goblin target 1", GOBLIN_TARGET);
    goblinTarget2 = makeIntVar("goblin target 2", GOBLIN_TARGET);
    goblinTarget3 = makeIntVar("goblin target 3", GOBLIN_TARGET);
    goblinTarget4 = makeIntVar("goblin target 4", GOBLIN_TARGET);
    goblinTarget5 = makeIntVar("goblin target 5", GOBLIN_TARGET);
    model.addVariable(goblinHp1);
    model.addVariable(goblinHp2);
    model.addVariable(goblinHp3);
    model.addVariable(goblinHp4);
    model.addVariable(goblinHp5);
    model.addVariable(goblinTarget1);
    model.addVariable(goblinTarget2);
    model.addVariable(goblinTarget3);
    model.addVariable(goblinTarget4);
    model.addVariable(goblinTarget5);
  }

  private IntegerVariable[] prepareHeuristic() {
    IntegerVariable[] instantiationOrder = new IntegerVariable[30];
    instantiationOrder[0] = warriorTarget1;
    instantiationOrder[1] = orcHp1;
    instantiationOrder[2] = goblinHp1;
    instantiationOrder[3] = orcTarget1;
    instantiationOrder[4] = goblinTarget1;
    instantiationOrder[5] = warriorHp1;
    instantiationOrder[6] = warriorTarget2;
    instantiationOrder[7] = orcHp2;
    instantiationOrder[8] = goblinHp2;
    instantiationOrder[9] = orcTarget2;
    instantiationOrder[10] = goblinTarget2;
    instantiationOrder[11] = warriorHp2;
    instantiationOrder[12] = warriorTarget3;
    instantiationOrder[13] = orcHp3;
    instantiationOrder[14] = goblinHp3;
    instantiationOrder[15] = orcTarget3;
    instantiationOrder[16] = goblinTarget3;
    instantiationOrder[17] = warriorHp3;
    instantiationOrder[18] = warriorTarget4;
    instantiationOrder[19] = orcHp4;
    instantiationOrder[20] = goblinHp4;
    instantiationOrder[21] = orcTarget4;
    instantiationOrder[22] = goblinTarget4;
    instantiationOrder[23] = warriorHp4;
    instantiationOrder[24] = warriorTarget5;
    instantiationOrder[25] = orcHp5;
    instantiationOrder[26] = goblinHp5;
    instantiationOrder[27] = orcTarget5;
    instantiationOrder[28] = goblinTarget5;
    instantiationOrder[29] = warriorHp5;

    return instantiationOrder;

  }

}
