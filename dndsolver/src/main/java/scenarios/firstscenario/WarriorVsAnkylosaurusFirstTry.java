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
 * Dans ce scénario, les deux combattants sont déjà au corps à
 * corps et le guerrier agira en premier.
 * 
 */
public class WarriorVsAnkylosaurusFirstTry {
  private static final int NB_OF_ROUNDS = 10;
  private static final int[] WARRIOR_DMG = { 0, 46 };
  private static final int[] ANKYLOSAURUS_DMG = { 0, 25 };
  private static final int WARRIOR_MAX_HP = 89;
  private static final int ANKYLOSAURUS_MAX_HP = 75;

  private Model model;
  private Solver solveur;
  private IntegerVariable[] warriorDamageReceived = new IntegerVariable[NB_OF_ROUNDS];
  private IntegerVariable[] ankylosaurusDamageReceived = new IntegerVariable[NB_OF_ROUNDS];
  private IntegerVariable warriorHp;
  private IntegerVariable endOfFight;

  public WarriorVsAnkylosaurusFirstTry() {
    model = new CPModel();

    // Variable contenant l'indice de la round à laquelle le combat se termine
    endOfFight = makeIntVar("end of fight", 0, 9);
    model.addVariable(endOfFight);

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
    // for (int i = 0; i < NB_OF_ROUNDS; i++) {
    // IntegerVariable[] tempWarriorArrays = Arrays.copyOf(warriorDamageReceived, i);
    // Constraint gThen = gt(sum(tempWarriorArrays), WARRIOR_MAX_HP);
    // model.addConstraint(ifThenElse(gThen, eq(endOfFight, i), FALSE));
    // for (int j = (i + 1); j < NB_OF_ROUNDS; j++) {
    // model.addConstraint(implies(eq(endOfFight, i), eq(warriorDamageReceived[j], 0)));
    // }
    // }

    // Contraintes qui vérifie si l'ankylosaure est vivant après le round i
    for (int i = 0; i < NB_OF_ROUNDS; i++) {
      IntegerVariable[] tempWarriorArrays = Arrays.copyOf(warriorDamageReceived, i);
      IntegerVariable[] tempAnkylosaurusArrays = Arrays.copyOf(ankylosaurusDamageReceived, i);
      Constraint hpContrainst = or(gt(sum(tempWarriorArrays), WARRIOR_MAX_HP),
                                   gt(sum(tempAnkylosaurusArrays), ANKYLOSAURUS_MAX_HP));
      model.addConstraint(ifThenElse(hpContrainst, eq(endOfFight, i), TRUE));
      for (int j = (i + 1); j < NB_OF_ROUNDS; j++) {
        model.addConstraint(implies(eq(endOfFight, i), eq(ankylosaurusDamageReceived[j], 0)));
      }
    }

    // Variable représentant la vie du guerrier
    warriorHp = makeIntVar("warrior HP", 0, WARRIOR_MAX_HP);
    model.addVariable(warriorHp);
    model.addConstraint(gt(warriorHp, 0));
    model.addConstraint(eq(warriorHp, minus(WARRIOR_MAX_HP, sum(warriorDamageReceived))));
  }

  public void solveScenario() {
    solveur = new CPSolver();
    // Lecture du modele par le solveur
    solveur.read(model);
    solveur.getConstraintIterator();
    solveur.setVarIntSelector(new StaticVarOrder(solveur, solveur.getVar(warriorDamageReceived)));
    if (solveur.solve()) {
      solveur.printRuntimeStatistics();
      System.out.println("Le combat s'est terminé à la round "
                         + solveur.getVar(endOfFight).getVal());
      System.out.println("Le guerrier a " + solveur.getVar(warriorHp).getVal() + " point de vie");
    } else {
      System.out.println("Aucune solution trouvee.");
    }

    System.out.println("Probleme resolu en " + solveur.getTimeCount() + " millisecondes.");
    System.out.println("Probleme resolu avec " + solveur.getBackTrackCount() + " retours arrieres.");
  }
}
