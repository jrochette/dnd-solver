package scenarios.firstscenario;

import static choco.Choco.*;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

public class WarriorVsAnkylosaurModelTwo {
  private static final int NB_OF_ROUNDS = 3;

  private static final int WARRIOR_MAX_HP = 89;
  private static final int WARRIOR_DMG = 46;
  private static final int[] WARRIOR_TARGET = { 0, 1 };

  private static final int ANKYLOSAUR_MAX_HP = 75;
  private static final int ANKYLOSAUR_DMG = 25;
  private static final int[] ANKYLOSAUR_TARGET = { 0, 2 };

  private Model model;
  private Solver solveur;

  // The warrior will be identified by 1
  private IntegerVariable warriorHp1;
  private IntegerVariable warriorHp2;
  private IntegerVariable warriorHp3;
  private IntegerVariable warriorTarget1;
  private IntegerVariable warriorTarget2;
  private IntegerVariable warriorTarget3;

  // The ankylosaur will be identified by 2
  private IntegerVariable ankylosaurHp1;
  private IntegerVariable ankylosaurHp2;
  private IntegerVariable ankylosaurHp3;
  private IntegerVariable ankylosaurTarget1;
  private IntegerVariable ankylosaurTarget2;
  private IntegerVariable ankylosaurTarget3;

  private IntegerVariable finalPartyHp;

  public WarriorVsAnkylosaurModelTwo() {
    model = new CPModel();

    prepareVariables();
    prepareRound1Constraints();
    prepareRound2Constraints();
    prepareRound3Constraints();

    finalPartyHp = makeIntVar("final party hp", 0, WARRIOR_MAX_HP, Options.V_OBJECTIVE);
    model.addVariable(finalPartyHp);
    model.addConstraint(eq(finalPartyHp, warriorHp3));

  }

  private void prepareVariables() {
    warriorHp1 = makeIntVar("warrior hp", 57, WARRIOR_MAX_HP);
    warriorHp2 = makeIntVar("warrior hp", 25, WARRIOR_MAX_HP);
    warriorHp3 = makeIntVar("warrior hp", -7, WARRIOR_MAX_HP);
    warriorTarget1 = makeIntVar("warrior target 1", WARRIOR_TARGET);
    warriorTarget2 = makeIntVar("warrior target 2", WARRIOR_TARGET);
    warriorTarget3 = makeIntVar("warrior target 3", WARRIOR_TARGET);
    model.addVariable(warriorHp1);
    model.addVariable(warriorHp2);
    model.addVariable(warriorHp3);
    model.addVariable(warriorTarget1);
    model.addVariable(warriorTarget2);
    model.addVariable(warriorTarget3);

    ankylosaurHp1 = makeIntVar("ankylosaur hp", 20, ANKYLOSAUR_MAX_HP);
    ankylosaurHp2 = makeIntVar("ankylosaur hp", -26, ANKYLOSAUR_MAX_HP);
    ankylosaurHp3 = makeIntVar("ankylosaur hp", -72, ANKYLOSAUR_MAX_HP);
    ankylosaurTarget1 = makeIntVar("ankylosaur target 1", ANKYLOSAUR_TARGET);
    ankylosaurTarget2 = makeIntVar("ankylosaur target 2", ANKYLOSAUR_TARGET);
    ankylosaurTarget3 = makeIntVar("ankylosaur target 3", ANKYLOSAUR_TARGET);
    model.addVariable(ankylosaurHp1);
    model.addVariable(ankylosaurHp2);
    model.addVariable(ankylosaurHp3);
    model.addVariable(ankylosaurTarget1);
    model.addVariable(ankylosaurTarget2);
    model.addVariable(ankylosaurTarget3);
  }

  private void prepareRound1Constraints() {
    // Choix des cibles
    Constraint ankylosaurDead = lt(ankylosaurHp1, 0);

    model.addConstraint(ifThenElse(ankylosaurDead,
                                   eq(ankylosaurTarget1, 0),
                                   neq(ankylosaurTarget1, 0)));

    model.addConstraint(neq(warriorTarget1, 0));

    // Résultat des attaques de warrior
    Constraint ankylosaurAttackedByWarrior = eq(warriorTarget1, 1);
    Constraint ankylosaurNotAttacked = eq(warriorTarget1, 0);
    model.addConstraint(implies(ankylosaurAttackedByWarrior,
                                eq(ankylosaurHp1, ANKYLOSAUR_MAX_HP - WARRIOR_DMG)));
    model.addConstraint(implies(ankylosaurNotAttacked, eq(ankylosaurHp1, ANKYLOSAUR_MAX_HP)));

    // Résultat des attaques de l'ankylosaur
    Constraint warriorAttackedByAnkylosaur = eq(ankylosaurTarget1, 2);
    Constraint warriorNotAttacked = eq(ankylosaurTarget1, 0);
    model.addConstraint(implies(warriorAttackedByAnkylosaur,
                                eq(warriorHp1, WARRIOR_MAX_HP - ANKYLOSAUR_DMG)));
    model.addConstraint(implies(warriorNotAttacked, eq(warriorTarget1, WARRIOR_MAX_HP)));

  }

  private void prepareRound2Constraints() {
    // Contrainte pour le choix des cibles des attaques
    Constraint ankylosaurDead = lt(ankylosaurHp2, 0);
    Constraint warriorDead = lt(warriorHp2, 0);

    model.addConstraint(ifThenElse(ankylosaurDead,
                                   eq(ankylosaurTarget2, 0),
                                   neq(ankylosaurTarget2, 0)));

    model.addConstraint(ifThenElse(warriorDead, eq(warriorTarget2, 0), neq(warriorTarget2, 0)));

    // Résultat des attaques de warrior
    Constraint ankylosaurAttackedByWarrior = eq(warriorTarget2, 1);
    Constraint ankylosaurNotAttacked = eq(warriorTarget2, 0);
    model.addConstraint(implies(ankylosaurAttackedByWarrior,
                                eq(ankylosaurHp2, minus(ankylosaurHp1, WARRIOR_DMG))));
    model.addConstraint(implies(ankylosaurNotAttacked, eq(ankylosaurHp2, ankylosaurHp1)));

    // Résultat des attaques de l'ankylosaur
    Constraint warriorAttackedByAnkylosaur = eq(ankylosaurTarget2, 2);
    Constraint warriorNotAttacked = eq(ankylosaurTarget2, 0);
    model.addConstraint(implies(warriorAttackedByAnkylosaur,
                                eq(warriorHp2, minus(warriorHp1, ANKYLOSAUR_DMG))));
    model.addConstraint(implies(warriorNotAttacked, eq(warriorHp2, warriorHp1)));
  }

  private void prepareRound3Constraints() {
    // Contrainte pour le choix des cibles des attaques
    Constraint ankylosaurDead = lt(ankylosaurHp3, 0);
    Constraint warriorDead = lt(warriorHp3, 0);

    model.addConstraint(ifThenElse(ankylosaurDead,
                                   eq(ankylosaurTarget3, 0),
                                   neq(ankylosaurTarget3, 0)));

    model.addConstraint(ifThenElse(warriorDead, eq(warriorTarget3, 0), neq(warriorTarget3, 0)));

    // Résultat des attaques de warrior
    Constraint ankylosaurAttackedByWarrior = eq(warriorTarget3, 1);
    Constraint ankylosaurNotAttacked = eq(warriorTarget3, 0);
    model.addConstraint(implies(ankylosaurAttackedByWarrior,
                                eq(ankylosaurHp3, minus(ankylosaurHp2, WARRIOR_DMG))));
    model.addConstraint(implies(ankylosaurNotAttacked, eq(ankylosaurHp3, ankylosaurHp2)));

    // Résultat des attaques de l'ankylosaur
    Constraint warriorAttackedByAnkylosaur = eq(ankylosaurTarget3, 2);
    Constraint warriorNotAttacked = eq(ankylosaurTarget3, 0);
    model.addConstraint(implies(warriorAttackedByAnkylosaur,
                                eq(warriorHp3, minus(warriorHp2, ANKYLOSAUR_DMG))));
    model.addConstraint(implies(warriorNotAttacked, eq(warriorHp3, warriorHp2)));
  }

  public void solveScenario() {
    solveur = new CPSolver();
    // Lecture du modele par le solveur
    solveur.read(model);
    solveur.setVarIntSelector(new StaticVarOrder(solveur, solveur.getVar(prepareHeuristic())));
    if (solveur.maximize(false)) {
      System.out.println("Warrior final hp = " + solveur.getVar(warriorHp3).getVal());
      System.out.println("----------------------------");
      System.out.println("ankylosaur final hp = " + solveur.getVar(ankylosaurHp3).getVal());
      System.out.println("----------------------------");
      System.out.println("ROUND 1");
      System.out.println("Warrior target = " + solveur.getVar(warriorTarget1).getVal());
      System.out.println("ankylosaur target = " + solveur.getVar(ankylosaurTarget1).getVal());
      System.out.println("----------------------------");
      System.out.println("Warrior hp after round 1 = " + solveur.getVar(warriorHp1).getVal());
      System.out.println("ankylosaur hp after round 1 = " + solveur.getVar(ankylosaurHp1).getVal());
      System.out.println("----------------------------");
      System.out.println("ROUND 2");
      System.out.println("Warrior target = " + solveur.getVar(warriorTarget2).getVal());
      System.out.println("ankylosaur target = " + solveur.getVar(ankylosaurTarget2).getVal());
      System.out.println("----------------------------");
      System.out.println("Warrior hp after round 2 = " + solveur.getVar(warriorHp2).getVal());
      System.out.println("ankylosaur hp after round 2 = " + solveur.getVar(ankylosaurHp2).getVal());
      System.out.println("----------------------------");
      System.out.println("ROUND 3");
      System.out.println("Warrior target = " + solveur.getVar(warriorTarget3).getVal());
      System.out.println("ankylosaur target = " + solveur.getVar(ankylosaurTarget3).getVal());
      System.out.println("----------------------------");
      System.out.println("Warrior hp after round 3 = " + solveur.getVar(warriorHp3).getVal());
      System.out.println("ankylosaur hp after round 3 = " + solveur.getVar(ankylosaurHp3).getVal());
      System.out.println("----------------------------");
      System.out.println("Total party life = " + solveur.getVar(finalPartyHp).getVal());
    } else {
      System.out.println("Aucune solution trouvee.");
    }
    System.out.println("--------------------------------");
    System.out.println("Probleme resolu en " + solveur.getTimeCount() + " millisecondes.");
    System.out.println("Probleme resolu avec " + solveur.getBackTrackCount() + " retours arrieres.");
  }

  private IntegerVariable[] prepareHeuristic() {
    IntegerVariable[] instantiationOrder = new IntegerVariable[12];
    instantiationOrder[0] = warriorTarget1;
    instantiationOrder[1] = ankylosaurHp1;
    instantiationOrder[2] = ankylosaurTarget1;
    instantiationOrder[3] = warriorHp1;
    instantiationOrder[4] = warriorTarget2;
    instantiationOrder[5] = ankylosaurHp2;
    instantiationOrder[6] = ankylosaurTarget2;
    instantiationOrder[7] = warriorHp2;
    instantiationOrder[8] = warriorTarget3;
    instantiationOrder[9] = ankylosaurHp3;
    instantiationOrder[10] = ankylosaurTarget3;
    instantiationOrder[11] = warriorHp3;

    return instantiationOrder;

  }
}
