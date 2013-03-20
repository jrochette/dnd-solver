import static choco.Choco.*;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

public class DndFightSolver {
  private static final int n = 5;
  private static final int p = 16;

  private static Model model;
  private static Solver solveur;
  private static IntegerVariable[] debuts;
  private static IntegerVariable[] pauses;
  private static IntegerVariable[] fins;
  private static IntegerVariable[] offres;
  private static IntegerConstantVariable[] demandes;

  public static void main(String[] args) {
    model = new CPModel();

    demandes = new IntegerConstantVariable[p];
    demandes[0] = new IntegerConstantVariable(1);
    demandes[1] = new IntegerConstantVariable(2);
    demandes[2] = new IntegerConstantVariable(3);
    demandes[3] = new IntegerConstantVariable(4);
    demandes[4] = new IntegerConstantVariable(5);
    demandes[5] = new IntegerConstantVariable(4);
    demandes[6] = new IntegerConstantVariable(2);
    demandes[7] = new IntegerConstantVariable(3);
    demandes[8] = new IntegerConstantVariable(4);
    demandes[9] = new IntegerConstantVariable(3);
    demandes[10] = new IntegerConstantVariable(5);
    demandes[11] = new IntegerConstantVariable(5);
    demandes[12] = new IntegerConstantVariable(4);
    demandes[13] = new IntegerConstantVariable(3);
    demandes[14] = new IntegerConstantVariable(3);
    demandes[15] = new IntegerConstantVariable(3);

    // Construction des tableaux de variables représentant les employés
    debuts = new IntegerVariable[n];
    pauses = new IntegerVariable[n];
    fins = new IntegerVariable[n];
    for (int i = 0; i < n; i++) {
      debuts[i] = makeIntVar("debut " + i, 0, 6);
      pauses[i] = makeIntVar("pause " + i, 4, 11);
      fins[i] = makeIntVar("fin " + i, 9, 15);
      model.addVariable(debuts[i]);
      model.addVariable(pauses[i]);
      model.addVariable(fins[i]);
      model.addConstraint(leq(minus(fins[i], debuts[i]), 14));
      model.addConstraint(geq(minus(fins[i], debuts[i]), 10));
      model.addConstraint(geq(minus(fins[i], pauses[i]), 4));
      model.addConstraint(geq(minus(pauses[i], debuts[i]), 4));
    }

    // Construction du tableau d'offre
    offres = new IntegerVariable[p];
    for (int i = 0; i < p; i++) {
      offres[i] = makeIntVar("periode " + i, 1, 5);
      model.addVariable(offres[i]);
      IntegerExpressionVariable[] sommeOffre = new IntegerExpressionVariable[n];
      for (int j = 0; j < n; j++) {
        sommeOffre[j] = ifThenElse(and(geq(i, debuts[j]), leq(i, fins[j]), neq(i, pauses[j])),
                                   ONE,
                                   ZERO);
      }
      model.addConstraint(eq(offres[i], sum(sommeOffre)));
    }

    // Calcul du coût pour chaque période
    IntegerVariable[] cout = new IntegerVariable[p];
    for (int i = 0; i < p; i++) {
      cout[i] = makeIntVar("cout " + i, 0, 4);
      model.addVariable(cout[i]);
      model.addConstraint(eq(cout[i], abs(minus(offres[i], demandes[i]))));
    }

    // Calcul du coût total
    IntegerVariable coutTotal = makeIntVar("Coût total", 0, 64, Options.V_OBJECTIVE);
    model.addVariable(coutTotal);
    model.addConstraint(eq(coutTotal, sum(cout)));

    solveur = new CPSolver();
    // Lecture du modele par le solveur
    solveur.read(model);
    // Le solveur minimize
    if (solveur.minimize(true)) {
      System.out.println("Le coût total est de : " + (solveur.getVar(coutTotal).getVal() * 20)
                         + "$.");
      for (int i = 0; i < n; i++) {
        printScheduleForEmployee(i);
      }
      printOffre();
      printDemande();
    } else {
      System.out.println("Aucune solution trouvee.");
    }

    System.out.println("Probleme resolu en " + solveur.getTimeCount() + " millisecondes.");
    System.out.println("Probleme resolu avec " + solveur.getBackTrackCount() + " retours arrieres.");
  }

  private static void printScheduleForEmployee(int employeeNumber) {
    System.out.println("Horaire de l'employé " + employeeNumber + ".");
    System.out.println("Début : " + solveur.getVar(debuts[employeeNumber]).getVal());
    System.out.println("Pause : " + solveur.getVar(pauses[employeeNumber]).getVal());
    System.out.println("Fin : " + solveur.getVar(fins[employeeNumber]).getVal());
  }

  private static void printOffre() {
    System.out.print("Offre   : ");
    for (int i = 0; i < p; i++) {
      System.out.print(solveur.getVar(offres[i]).getVal() + " ");
    }
    System.out.println("");
  }

  private static void printDemande() {
    System.out.print("Demande : ");
    for (int i = 0; i < p; i++) {
      System.out.print(solveur.getVar(demandes[i]).getVal() + " ");
    }
    System.out.println("");

  }
}
