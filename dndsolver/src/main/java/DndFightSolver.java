import scenarios.TwoWarsTwoHobLoops.TwoWarriorsVsTwoHobgoblins;
import scenarios.firstscenario.WarriorVsAnkylosaurusScenarioWithArrays;
import scenarios.second.WarriorVsTwoHobgoblins;
import scenarios.third.TwoWarriorVsTwoOrcs;

public class DndFightSolver {

  public static void main(String[] args) {
    // Scénario 1
    WarriorVsAnkylosaurusScenarioWithArrays firstScenario = new WarriorVsAnkylosaurusScenarioWithArrays();
    System.out.println("RUNNING SCENARIO 1");
    System.out.println("");
    firstScenario.solveScenario();

    System.out.println("__________________________________________________________________________");

    // Scénario 2
    WarriorVsTwoHobgoblins secondScenario = new WarriorVsTwoHobgoblins();
    System.out.println("RUNNING SCENARIO 2");
    System.out.println("");
    secondScenario.solveScenario();

    System.out.println("__________________________________________________________________________");

    // Scénario 3
    TwoWarriorVsTwoOrcs thirdScenario = new TwoWarriorVsTwoOrcs();
    System.out.println("RUNNING THE SCENARIO 3");
    System.out.println("");
    thirdScenario.solveScenario();

    System.out.println("__________________________________________________________________________");

    // Scénario 4
    TwoWarriorsVsTwoHobgoblins fourthScenario = new TwoWarriorsVsTwoHobgoblins();
    System.out.println("RUNNING SCENARIO 4 (not working)");
    System.out.println("");
    fourthScenario.solveScenario();

  }
}
