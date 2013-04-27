import scenarios.TwoWarsTwoHobLoops.TwoWarriorsVsTwoHobgoblins;
import scenarios.firstscenario.WarriorVsAnkylosaurusScenarioWithArrays;
import scenarios.second.WarriorVsTwoHobgoblins;

public class DndFightSolver {

  public static void main(String[] args) {
    // Scénario 1
    WarriorVsAnkylosaurusScenarioWithArrays firstScenario = new WarriorVsAnkylosaurusScenarioWithArrays();
    System.out.println("Running the first try of scenario 1");
    firstScenario.solveScenario();

    System.out.println("--------------------------");
    System.out.println("");
    System.out.println("--------------------------");

    // Scénario 2
    WarriorVsTwoHobgoblins secondScenario = new WarriorVsTwoHobgoblins();
    System.out.println("Running the scenario 2");
    secondScenario.solveScenario();
    
    System.out.println("--------------------------");
    System.out.println("");
    System.out.println("--------------------------");
    
    // Scénario 3
    TwoWarriorsVsTwoHobgoblins thirdScenario = new TwoWarriorsVsTwoHobgoblins();
    System.out.println("Running the scenario 3");
    thirdScenario.solveScenario();
  }

}
