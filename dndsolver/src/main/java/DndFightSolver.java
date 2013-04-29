import scenarios.TwoWarsTwoHobLoops.TwoWarriorsVsTwoHobgoblins;
import scenarios.firstscenario.WarriorVsAnkylosaurModelTwo;
import scenarios.firstscenario.WarriorVsAnkylosaurScenarioWithoutArrayModelOne;
import scenarios.second.WarriorVsTwoHobgoblins;
import scenarios.third.TwoWarriorVsTwoOrcs;

public class DndFightSolver {

  public static void main(String[] args) {
    // Scénario 1
    runScenario1();

    // Scénario 2
    // runScenario2();

    // Scénario 3
    // runScenario3();

    // Scénario 4
    // runScenario4();

  }

  private static void runScenario1() {
    WarriorVsAnkylosaurScenarioWithoutArrayModelOne firstScenarioModelOne = new WarriorVsAnkylosaurScenarioWithoutArrayModelOne();
    WarriorVsAnkylosaurModelTwo firstScenariorModelTwo = new WarriorVsAnkylosaurModelTwo();
    System.out.println("RUNNING SCENARIO 1, MODEL 1");
    System.out.println("");

    firstScenarioModelOne.solveScenario();

    System.out.println("");
    System.out.println("RUNNING SCENARIO 1, MODEL 2");
    System.out.println("");

    firstScenariorModelTwo.solveScenario();

    System.out.println("__________________________________________________________________________");
  }

  private static void runScenario2() {
    WarriorVsTwoHobgoblins secondScenario = new WarriorVsTwoHobgoblins();
    System.out.println("RUNNING SCENARIO 2");
    System.out.println("");
    secondScenario.solveScenario();

    System.out.println("__________________________________________________________________________");
  }

  private static void runScenario3() {
    TwoWarriorVsTwoOrcs thirdScenario = new TwoWarriorVsTwoOrcs();
    System.out.println("RUNNING THE SCENARIO 3");
    System.out.println("");
    thirdScenario.solveScenario();

    System.out.println("__________________________________________________________________________");
  }

  private static void runScenario4() {
    TwoWarriorsVsTwoHobgoblins fourthScenario = new TwoWarriorsVsTwoHobgoblins();
    System.out.println("RUNNING SCENARIO 4 (not working)");
    System.out.println("");
    fourthScenario.solveScenario();
  }
}
