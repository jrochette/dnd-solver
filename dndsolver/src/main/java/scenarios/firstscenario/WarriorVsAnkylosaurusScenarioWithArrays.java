package scenarios.firstscenario;

import static choco.Choco.*;

import java.util.Arrays;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

/*
 * Ce scénario représente un combat entre un guerrier (niveau 10)
 * et un ennemi (ankylosaure) tiré du bestiaire du jeu de rôle
 * pathfinder (un version de donjons et dragons)
 * 
 * Le guerrier à 167 points de vie et fait 46 dmg par attaque
 * 
 * L'ankylosaure à 223 points de vie et fait 25 dmg par attaque
 * 
 * Dans le cadre de ce scénario, c'est le guerrier qui agira
 * en premier.
 * 
 * Pour garder ce scénario très simple, il n'y aura pas de mouvement
 * de la part du guerrier ni de l'ankylosaure.
 * 
 */
public class WarriorVsAnkylosaurusScenarioWithArrays {
  private static final int NB_OF_ROUNDS = 10;
  private static final int[] WARRIOR_DMG = { 0, 46 };
  private static final int[] ANKYLOSAURUS_DMG = { 0, 25 };
  private static final int WARRIOR_MAX_HP = 167;
  private static final int ANKYLOSAURUS_MAX_HP = 223;

  private Model model;
  private Solver solveur;
  private IntegerVariable[] warriorDamageReceived = new IntegerVariable[NB_OF_ROUNDS];
  private IntegerVariable[] ankylosaurusDamageReceived = new IntegerVariable[NB_OF_ROUNDS];
  private IntegerVariable warriorHp;
  private IntegerVariable ankylosaurusHp;

  public WarriorVsAnkylosaurusScenarioWithArrays() {
    model = new CPModel();

    // Tableau de variable représentant les dommages reçus par le guerrier
    for (int i = 0; i < NB_OF_ROUNDS; i++) {
      warriorDamageReceived[i] = makeIntVar("warrior damage received for round" + (i + 1),
                                            ANKYLOSAURUS_DMG);
      model.addVariable(warriorDamageReceived[i]);
    }

    // Tableau de variable représentant les dommages reçus par le guerrier
    for (int i = 0; i < NB_OF_ROUNDS; i++) {
      ankylosaurusDamageReceived[i] = makeIntVar("ankylosaurus damage received for round" + (i + 1),
                                                 WARRIOR_DMG);
      model.addVariable(ankylosaurusDamageReceived[i]);
    }

    // Contraintes qui vérifie si le guerrier est vivant après le round i
    for (int i = 0; i < NB_OF_ROUNDS; i++) {
      IntegerVariable[] tempWarriorArrays = Arrays.copyOf(warriorDamageReceived, i);
      IntegerVariable[] tempAnkylosaureArrays = Arrays.copyOf(ankylosaurusDamageReceived, i);
      IntegerVariable[] tempAnkylosaureArrays2 = Arrays.copyOf(ankylosaurusDamageReceived, i + 1);

      Constraint gtWarrior = gt(sum(tempWarriorArrays), WARRIOR_MAX_HP);
      Constraint gtAnkylosaure = gt(sum(tempAnkylosaureArrays), ANKYLOSAURUS_MAX_HP);
      Constraint gtAnkylosaureForNextRound = gt(sum(tempAnkylosaureArrays2), ANKYLOSAURUS_MAX_HP);

      // dommage reçu par l'ankylosaure
      model.addConstraint(ifThenElse(gtWarrior,
                                     eq(ankylosaurusDamageReceived[i], 0),
                                     ifThenElse(gtAnkylosaure,
                                                eq(ankylosaurusDamageReceived[i], 0),
                                                eq(ankylosaurusDamageReceived[i], 46))));

      // dommage reçu par le guerrier
      model.addConstraint(ifThenElse(gtAnkylosaure,
                                     eq(warriorDamageReceived[i], 0),
                                     ifThenElse(gtAnkylosaureForNextRound,
                                                eq(warriorDamageReceived[i], 0),
                                                eq(warriorDamageReceived[i], 25))));
    }

    // Variable représentant la vie du guerrier
    warriorHp = makeIntVar("warrior HP", -50, WARRIOR_MAX_HP);
    model.addVariable(warriorHp);
    model.addConstraint(eq(warriorHp, minus(WARRIOR_MAX_HP, sum(warriorDamageReceived))));
    // Variable représentant la vie d
    ankylosaurusHp = makeIntVar("ankylosaurus HP", -6300, ANKYLOSAURUS_MAX_HP);
    model.addVariable(ankylosaurusHp);
    model.addConstraint(eq(ankylosaurusHp,
                           minus(ANKYLOSAURUS_MAX_HP, sum(ankylosaurusDamageReceived))));
  }

  public void solveScenario() {
    solveur = new CPSolver();
    // Lecture du modele par le solveur
    solveur.read(model);
    solveur.setVarIntSelector(new StaticVarOrder(solveur, solveur.getVar(prepareHeuristic())));
    if (solveur.solve()) {
      System.out.println("Le guerrier a " + solveur.getVar(warriorHp).getVal() + " point de vie");
      System.out.println("L'ankylosaure a " + solveur.getVar(ankylosaurusHp).getVal()
                         + " point de vie");
      System.out.println("--------------------------------");
      for (int i = 0; i < NB_OF_ROUNDS; i++) {
        System.out.println("Dmg par guerrier pour le round " + (i + 1) + " : "
                           + solveur.getVar(ankylosaurusDamageReceived[i]).getVal());
        System.out.println("Dmg par ankylosaure pour le round " + (i + 1) + " : "
                           + solveur.getVar(warriorDamageReceived[i]).getVal());
      }

    } else {
      System.out.println("Aucune solution trouvee.");
    }
    System.out.println("--------------------------------");
    System.out.println("Probleme resolu en " + solveur.getTimeCount() + " millisecondes.");
    System.out.println("Probleme resolu avec " + solveur.getBackTrackCount() + " retours arrieres.");
  }

  private IntegerVariable[] prepareHeuristic() {
    IntegerVariable[] instantiationOrder = new IntegerVariable[20];
    instantiationOrder[0] = ankylosaurusDamageReceived[0];
    instantiationOrder[1] = warriorDamageReceived[0];
    instantiationOrder[2] = ankylosaurusDamageReceived[1];
    instantiationOrder[3] = warriorDamageReceived[1];
    instantiationOrder[4] = ankylosaurusDamageReceived[2];
    instantiationOrder[5] = warriorDamageReceived[2];
    instantiationOrder[6] = ankylosaurusDamageReceived[3];
    instantiationOrder[7] = warriorDamageReceived[3];
    instantiationOrder[8] = ankylosaurusDamageReceived[4];
    instantiationOrder[9] = warriorDamageReceived[4];
    instantiationOrder[10] = ankylosaurusDamageReceived[5];
    instantiationOrder[11] = warriorDamageReceived[5];
    instantiationOrder[12] = ankylosaurusDamageReceived[6];
    instantiationOrder[13] = warriorDamageReceived[6];
    instantiationOrder[14] = ankylosaurusDamageReceived[7];
    instantiationOrder[15] = warriorDamageReceived[7];
    instantiationOrder[16] = ankylosaurusDamageReceived[8];
    instantiationOrder[17] = warriorDamageReceived[8];
    instantiationOrder[18] = ankylosaurusDamageReceived[9];
    instantiationOrder[19] = warriorDamageReceived[9];
    return instantiationOrder;

  }
}
