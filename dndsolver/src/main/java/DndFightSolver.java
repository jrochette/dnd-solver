import scenarios.firstscenario.WarriorVsAnkylosaurSecondTry;
import scenarios.firstscenario.WarriorVsAnkylosaurusFirstTry;

public class DndFightSolver {

  public static void main(String[] args) {
    // Scénario 1
    WarriorVsAnkylosaurusFirstTry firstScenario = new WarriorVsAnkylosaurusFirstTry();
    System.out.println("Running the first try of scenario 1");
    firstScenario.solveScenario();

    System.out.println("");

    // Scénario 2
    WarriorVsAnkylosaurSecondTry firstScenarioSecondTry = new WarriorVsAnkylosaurSecondTry();
    System.out.println("Running the second try of scenario 1");
    firstScenarioSecondTry.solveScenario();

  }

}
