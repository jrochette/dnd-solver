package scenarios.firstscenario;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.MaxVal;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
 * Ce scénario représente un combat entre un guerrier (niveau 10)
 * et un ennemi (ankylosaure) tiré du bestiaire du jeu de rôle
 * pathfinder (un version de donjons et dragons)
 * 
 * Le guerrier à 89 points de vie et fait 46 dmg par attaque
 * 
 * L'ankylosaure à 75 points de vie et fait 25 dmg par attaque
 * 
 * Dans le cadre de ce scénario, c'est le guerrier qui agira
 * en premier
 * 
 * Pour garder ce scénario très simple, il n'y aura pas de mouvement
 * de la part du guerrier ni de l'ankylosaure.
 *
 */
public class WarriorVsAnkylosaurScenarioWithoutArrayModelOne {
  private static final int NB_OF_ROUNDS = 3;
  private static final int[] WARRIOR_DMG = { 0, 46 };
  private static final int[] ANKYLOSAURUS_DMG = { 0, 25 };
  private static final int WARRIOR_MAX_HP = 89;
  private static final int ANKYLOSAURUS_MAX_HP = 75;

  private IntegerVariable warriorDmg1;
  private IntegerVariable warriorDmg2;
  private IntegerVariable warriorDmg3;

  private IntegerVariable ankylosaurusDmg1;
  private IntegerVariable ankylosaurusDmg2;
  private IntegerVariable ankylosaurusDmg3;

  private IntegerVariable warriorHp;
  private IntegerVariable ankylosaurusHp;
  private IntegerVariable endOfFight;

  private Model model;
  private Solver solveur;

  public WarriorVsAnkylosaurScenarioWithoutArrayModelOne() {
    // Création du model et du solveur
    model = new CPModel();

    // déclaration des variables représentant les dommages reçu par les combattans
    warriorDmg1 = makeIntVar("warrior damage received for round 1", ANKYLOSAURUS_DMG);
    warriorDmg2 = makeIntVar("warrior damage received for round 2", ANKYLOSAURUS_DMG);
    warriorDmg3 = makeIntVar("warrior damage received for round 3", ANKYLOSAURUS_DMG);
    model.addVariable(warriorDmg1);
    model.addVariable(warriorDmg2);
    model.addVariable(warriorDmg3);

    ankylosaurusDmg1 = makeIntVar("ankylosaurus damage received for round 1", WARRIOR_DMG);
    ankylosaurusDmg2 = makeIntVar("ankylosaurus damage received for round 2", WARRIOR_DMG);
    ankylosaurusDmg3 = makeIntVar("ankylosaurus damage received for round 3", WARRIOR_DMG);
    model.addVariable(ankylosaurusDmg1);
    model.addVariable(ankylosaurusDmg2);
    model.addVariable(ankylosaurusDmg3);

    // Variable contenant l'indice de la round à laquelle le combat se termine
    endOfFight = makeIntVar("end of fight", 1, NB_OF_ROUNDS);
    model.addVariable(endOfFight);

    // contrainte pour les dommages reçus par les combattants
    model.addConstraint(eq(ankylosaurusDmg1, 46));
    model.addConstraint(eq(warriorDmg1, 25));

    Constraint gtAnkylosaurus1 = gt(ankylosaurusDmg1, ANKYLOSAURUS_MAX_HP);
    Constraint gtWarrior1 = gt(warriorDmg1, WARRIOR_MAX_HP);
    Constraint gtAnkylosaurus2 = gt(sum(ankylosaurusDmg1, ankylosaurusDmg2), ANKYLOSAURUS_MAX_HP);
    Constraint gtWarrior2 = gt(sum(warriorDmg1, warriorDmg2), WARRIOR_MAX_HP);

    model.addConstraint(ifThenElse(gtAnkylosaurus1,
                                   eq(warriorDmg2, 0),
                                   ifThenElse(gtAnkylosaurus2,
                                              eq(warriorDmg2, 0),
                                              eq(warriorDmg2, 25))));// le second ifThenElse est dû
                                                                     // au fait que le guerrier joue
                                                                     // en premier dans un combat
    model.addConstraint(ifThenElse(gtAnkylosaurus2,
                                   eq(warriorDmg3, 0),
                                   ifThenElse(gtWarrior2, eq(warriorDmg3, 0), eq(warriorDmg3, 25))));

    model.addConstraint(ifThenElse(gtWarrior1, eq(ankylosaurusDmg2, 0), eq(ankylosaurusDmg2, 46)));
    model.addConstraint(ifThenElse(gtWarrior2,
                                   eq(ankylosaurusDmg3, 0),
                                   ifThenElse(gtAnkylosaurus2,
                                              eq(ankylosaurusDmg3, 0),
                                              eq(ankylosaurusDmg3, 46))));

    // Variable représentant la vie du guerrier
    warriorHp = makeIntVar("warrior HP", -50, WARRIOR_MAX_HP);
    model.addVariable(warriorHp);
    model.addConstraint(gt(warriorHp, 0));// le guerrier doit survivre
    model.addConstraint(eq(warriorHp,
                           minus(WARRIOR_MAX_HP, sum(warriorDmg1, warriorDmg2, warriorDmg3))));
    // Variable représentant la vie de l'ankylosaur
    ankylosaurusHp = makeIntVar("ankylosaurus HP", -63, ANKYLOSAURUS_MAX_HP);
    model.addVariable(ankylosaurusHp);
    model.addConstraint(eq(ankylosaurusHp,
                           minus(ANKYLOSAURUS_MAX_HP,
                                 sum(ankylosaurusDmg1, ankylosaurusDmg2, ankylosaurusDmg3))));
  }

  public void solveScenario() {
    solveur = new CPSolver();
    // Lecture du modele par le solveur
    solveur.read(model);

    // Préparation du solveur (heuristique de choix de variable et de valeur)
    solveur.getConstraintIterator();
    IntDomainVar[] heuristic = { solveur.getVar(ankylosaurusDmg1), solveur.getVar(warriorDmg1),
                                solveur.getVar(ankylosaurusDmg2), solveur.getVar(warriorDmg2),
                                solveur.getVar(ankylosaurusDmg3), solveur.getVar(warriorDmg3) };
    solveur.setVarIntSelector(new StaticVarOrder(solveur, heuristic));
    solveur.setValIntSelector(new MaxVal());

    // Solving ......
    if (solveur.solve()) {
      System.out.println("RÉSULTAT DU COMBAT APRÈS " + NB_OF_ROUNDS + " TOURS");
      System.out.println("--------------------------------");
      System.out.println("Le guerrier a " + solveur.getVar(warriorHp).getVal() + " point de vie");
      if (solveur.getVar(warriorHp).getVal() < 0) {
        System.out.println("Le guerrier est mort");
      }
      System.out.println("dmg received by warrior on round 1 : "
                         + solveur.getVar(warriorDmg1).getVal());
      System.out.println("dmg received by warrior on round 2 : "
                         + solveur.getVar(warriorDmg2).getVal());
      System.out.println("dmg received by warrior on round 3 : "
                         + solveur.getVar(warriorDmg3).getVal());

      System.out.println("--------------------------------");

      System.out.println("L'ankylosaure a " + solveur.getVar(ankylosaurusHp).getVal()
                         + " point de vie");
      if (solveur.getVar(ankylosaurusHp).getVal() < 0) {
        System.out.println("L'ankylosaure est mort");
      }
      System.out.println("dmg received by ankylosaurus on round 1 : "
                         + solveur.getVar(ankylosaurusDmg1).getVal());
      System.out.println("dmg received by ankylosaurus on round 2 : "
                         + solveur.getVar(ankylosaurusDmg2).getVal());
      System.out.println("dmg received by ankylosaurus on round 3 : "
                         + solveur.getVar(ankylosaurusDmg3).getVal());
    } else {
      System.out.println("Aucune solution trouvee.");
    }
    System.out.println("--------------------------------");
    System.out.println("Probleme resolu en " + solveur.getTimeCount() + " millisecondes.");
    System.out.println("Probleme resolu avec " + solveur.getBackTrackCount() + " retours arrieres.");
  }
}
