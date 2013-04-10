package scenarios.second;

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
 * et deux ennemis (hobgloblins) tiré du bestiaire du jeu de rôle
 * pathfinder (un version de donjons et dragons)
 * 
 * Le guerrier à 89 points de vie et fait 46 dmg par attaque
 * 
 * Les hobgoblins ont 48 points de vie chacun et font 12 dmg par attaque
 * 
 * Dans le cadre de ce scénario, c'est le guerrier qui agira
 * en premier.
 * 
 * Pour garder ce scénario très simple, il n'y aura pas de mouvement
 * de la part du guerrier ni des hobgoblins.
 * 
 */
public class WarriorVsTwoHobgoblins {
  private static final int NB_OF_ROUNDS = 10;
  private static final int[] WARRIOR_DMG = { 0, 46 };
  private static final int[] HOBGOBLIN_DMG = { 0, 12 };
  private static final int WARRIOR_MAX_HP = 89;
  private static final int HOBGOBLIN_MAX_HP = 48;

  private Model model;
  private Solver solveur;
  private IntegerVariable[] warriorDamageReceived = new IntegerVariable[NB_OF_ROUNDS];
  private IntegerVariable[] hobgoblin1DamageReceived = new IntegerVariable[NB_OF_ROUNDS];
  private IntegerVariable[] hobgoblin2DamageReceived = new IntegerVariable[NB_OF_ROUNDS];
  private IntegerVariable warriorHp;
  private IntegerVariable hobgoblin1Hp;
  private IntegerVariable hobgoblin2Hp;

  public WarriorVsTwoHobgoblins() {
    model = new CPModel();

    // Tableau de variable représentant les dommages reçus par le guerrier
    for (int i = 0; i < NB_OF_ROUNDS; i++) {
      warriorDamageReceived[i] = makeIntVar("warrior damage received for round" + (i + 1),
                                            HOBGOBLIN_DMG);
      model.addVariable(warriorDamageReceived[i]);
    }

    // Tableau de variable représentant les dommages reçus par l'hobgoblin 1
    for (int i = 0; i < NB_OF_ROUNDS; i++) {
      hobgoblin1DamageReceived[i] = makeIntVar("hobgoblin 1 damage received for round" + (i + 1),
                                               WARRIOR_DMG);
      model.addVariable(hobgoblin1DamageReceived[i]);
    }

    // Tableau de variable représentant les dommages reçus par l'hobgoblin 2
    for (int i = 0; i < NB_OF_ROUNDS; i++) {
      hobgoblin2DamageReceived[i] = makeIntVar("hobgoblin 1 damage received for round" + (i + 1),
                                               WARRIOR_DMG);
      model.addVariable(hobgoblin2DamageReceived[i]);
    }

    // Contraintes qui vérifie si le guerrier est vivant après le round i
    for (int i = 0; i < NB_OF_ROUNDS; i++) {
      IntegerVariable[] tempWarriorArrays = Arrays.copyOf(warriorDamageReceived, i);
      IntegerVariable[] tempHobgoblin1Arrays = Arrays.copyOf(hobgoblin1DamageReceived, i);
      IntegerVariable[] tempHobgolbin1Arrays2 = Arrays.copyOf(hobgoblin1DamageReceived, i + 1);
      IntegerVariable[] tempHobgoblin2Arrays = Arrays.copyOf(hobgoblin1DamageReceived, i);
      IntegerVariable[] tempHobgolbin2Arrays2 = Arrays.copyOf(hobgoblin1DamageReceived, i + 1);

      Constraint gtWarrior = gt(sum(tempWarriorArrays), WARRIOR_MAX_HP);
      Constraint gtHobgoblin1 = gt(sum(tempHobgoblin1Arrays), HOBGOBLIN_MAX_HP);
      Constraint gtHobgoblin1NextRound = gt(sum(tempHobgolbin1Arrays2), HOBGOBLIN_MAX_HP);
      Constraint gtHobgoblin2 = gt(sum(tempHobgoblin2Arrays), HOBGOBLIN_MAX_HP);
      Constraint gtHobgoblin2NextRound = gt(sum(tempHobgolbin2Arrays2), HOBGOBLIN_MAX_HP);

      // dommage reçu par l'hobgoblin 1
      model.addConstraint(ifThenElse(gtWarrior,
                                     eq(hobgoblin1DamageReceived[i], 0),
                                     ifThenElse(gtHobgoblin1,
                                                eq(hobgoblin1DamageReceived[i], 0),
                                                eq(hobgoblin1DamageReceived[i], 46))));

      // dommage reçu par l'hobgoblin 2
      model.addConstraint(ifThenElse(gtWarrior,
                                     eq(hobgoblin2DamageReceived[i], 0),
                                     ifThenElse(gtHobgoblin2,
                                                eq(hobgoblin2DamageReceived[i], 0),
                                                eq(hobgoblin2DamageReceived[i], 46))));

      // dommage reçu par le guerrier
      model.addConstraint(ifThenElse(gtHobgoblin1,
                                     eq(warriorDamageReceived[i], 0),
                                     ifThenElse(gtHobgoblin1NextRound,
                                                eq(warriorDamageReceived[i], 0),
                                                eq(warriorDamageReceived[i], 25))));
      model.addConstraint(ifThenElse(gtHobgoblin2,
                                     eq(warriorDamageReceived[i], 0),
                                     ifThenElse(gtHobgoblin2NextRound,
                                                eq(warriorDamageReceived[i], 0),
                                                eq(warriorDamageReceived[i], 25))));
      model.addConstraint(implies(eq(hobgoblin1DamageReceived[i], 46),
                                  eq(hobgoblin2DamageReceived[i], 0)));
    }

    // Variable représentant la vie du guerrier
    warriorHp = makeIntVar("warrior HP", -50, WARRIOR_MAX_HP);
    model.addVariable(warriorHp);
    model.addConstraint(eq(warriorHp, minus(WARRIOR_MAX_HP, sum(warriorDamageReceived))));
    // Variable représentant la vie de l'hobgoblin
    hobgoblin1Hp = makeIntVar("hobgoblin 1 HP", -6300, HOBGOBLIN_MAX_HP);
    model.addVariable(hobgoblin1Hp);
    model.addConstraint(eq(hobgoblin1Hp, minus(HOBGOBLIN_MAX_HP, sum(hobgoblin1DamageReceived))));
    // Variable représentant la vie de l'hobgoblin
    hobgoblin2Hp = makeIntVar("hobgoblin 2 HP", -6300, HOBGOBLIN_MAX_HP);
    model.addVariable(hobgoblin2Hp);
    model.addConstraint(eq(hobgoblin2Hp, minus(HOBGOBLIN_MAX_HP, sum(hobgoblin2DamageReceived))));
  }

  public void solveScenario() {
    solveur = new CPSolver();
    // Lecture du modele par le solveur
    solveur.read(model);
    solveur.setVarIntSelector(new StaticVarOrder(solveur, solveur.getVar(prepareHeuristic())));
    if (solveur.solve()) {
      System.out.println("Le guerrier a " + solveur.getVar(warriorHp).getVal() + " point de vie");
      System.out.println("L'ankylosaure a " + solveur.getVar(hobgoblin1Hp).getVal()
                         + " point de vie");
      System.out.println("--------------------------------");
      for (int i = 0; i < NB_OF_ROUNDS; i++) {
        System.out.println("Dmg recu par guerrier pour le round " + (i + 1) + " : "
                           + solveur.getVar(warriorDamageReceived[i]).getVal());
        System.out.println("Dmg recu par hobgoblin 1 pour le round " + (i + 1) + " : "
                           + solveur.getVar(hobgoblin1DamageReceived[i]).getVal());
        System.out.println("Dmg recu par hobgoblin 2 pour le round " + (i + 1) + " : "
                           + solveur.getVar(hobgoblin2DamageReceived[i]).getVal());
      }

    } else {
      System.out.println("Aucune solution trouvee.");
    }
    System.out.println("--------------------------------");
    System.out.println("Probleme resolu en " + solveur.getTimeCount() + " millisecondes.");
    System.out.println("Probleme resolu avec " + solveur.getBackTrackCount() + " retours arrieres.");
  }

  private IntegerVariable[] prepareHeuristic() {
    IntegerVariable[] instantiationOrder = new IntegerVariable[30];
    instantiationOrder[0] = hobgoblin1DamageReceived[0];
    instantiationOrder[1] = hobgoblin1DamageReceived[0];
    instantiationOrder[2] = warriorDamageReceived[0];
    instantiationOrder[3] = hobgoblin1DamageReceived[1];
    instantiationOrder[4] = hobgoblin1DamageReceived[1];
    instantiationOrder[5] = warriorDamageReceived[1];
    instantiationOrder[6] = hobgoblin1DamageReceived[2];
    instantiationOrder[7] = hobgoblin1DamageReceived[2];
    instantiationOrder[8] = warriorDamageReceived[2];
    instantiationOrder[9] = hobgoblin1DamageReceived[3];
    instantiationOrder[10] = hobgoblin1DamageReceived[3];
    instantiationOrder[11] = warriorDamageReceived[3];
    instantiationOrder[12] = hobgoblin1DamageReceived[4];
    instantiationOrder[13] = hobgoblin1DamageReceived[4];
    instantiationOrder[14] = warriorDamageReceived[4];
    instantiationOrder[15] = hobgoblin1DamageReceived[5];
    instantiationOrder[16] = hobgoblin1DamageReceived[5];
    instantiationOrder[17] = warriorDamageReceived[5];
    instantiationOrder[18] = hobgoblin1DamageReceived[6];
    instantiationOrder[19] = hobgoblin1DamageReceived[6];
    instantiationOrder[20] = warriorDamageReceived[6];
    instantiationOrder[21] = hobgoblin1DamageReceived[7];
    instantiationOrder[22] = hobgoblin1DamageReceived[7];
    instantiationOrder[23] = warriorDamageReceived[7];
    instantiationOrder[24] = hobgoblin1DamageReceived[8];
    instantiationOrder[25] = hobgoblin1DamageReceived[8];
    instantiationOrder[26] = warriorDamageReceived[8];
    instantiationOrder[27] = hobgoblin1DamageReceived[9];
    instantiationOrder[28] = hobgoblin1DamageReceived[9];
    instantiationOrder[29] = warriorDamageReceived[9];
    return instantiationOrder;

  }

}
