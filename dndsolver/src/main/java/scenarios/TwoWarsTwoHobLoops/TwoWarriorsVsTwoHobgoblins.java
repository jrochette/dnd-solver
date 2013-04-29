package scenarios.TwoWarsTwoHobLoops;

import static choco.Choco.*;

import java.util.Arrays;

import choco.Options;
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
 * Les guerriers à 89 points de vie chacun et font 46 dmg par attaque
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
public class TwoWarriorsVsTwoHobgoblins {

	private static final int NB_OF_ROUNDS = 10;
	private static final int[] WARRIOR_DMG = { 0, 46, 92 };
	private static final int[] HOBGOBLIN_DMG = { 0, 13, 26 };
	private static final int[] WARRIOR_TARGET = {  3, 4 };
	private static final int[] HOBGOBLIN_TARGET = {  1, 2 };
	private static final int WARRIOR_MAX_HP = 89;
	private static final int HOBGOBLIN_MAX_HP = 48;

	private Model model;
	private Solver solveur;
	private IntegerVariable[] warrior1DamageReceived = new IntegerVariable[NB_OF_ROUNDS];
	private IntegerVariable[] warrior2DamageReceived = new IntegerVariable[NB_OF_ROUNDS];
	private IntegerVariable[] hobgoblin1DamageReceived = new IntegerVariable[NB_OF_ROUNDS];
	private IntegerVariable[] hobgoblin2DamageReceived = new IntegerVariable[NB_OF_ROUNDS];

	private IntegerVariable[] warrior1DamageDone = new IntegerVariable[NB_OF_ROUNDS];
	private IntegerVariable[] warrior2DamageDone = new IntegerVariable[NB_OF_ROUNDS];
	private IntegerVariable[] hobgoblin1DamageDone = new IntegerVariable[NB_OF_ROUNDS];
	private IntegerVariable[] hobgoblin2DamageDone = new IntegerVariable[NB_OF_ROUNDS];

	private IntegerVariable warrior1Hp;
	private IntegerVariable warrior2Hp;
	private IntegerVariable hobgoblin1Hp;
	private IntegerVariable hobgoblin2Hp;

	public TwoWarriorsVsTwoHobgoblins() {
		model = new CPModel();

		// Tableau de variable représentant les dommages reçus par le premier
		// guerrier
		for (int i = 0; i < NB_OF_ROUNDS; i++) {
			warrior1DamageReceived[i] = makeIntVar(
					"warrior 1 damage received for round" + (i + 1),
					HOBGOBLIN_DMG);
			model.addVariable(warrior1DamageReceived[i]);
		}

		// Tableau de variable représentant les dommages reçus par le deuxième
		// guerrier
		for (int i = 0; i < NB_OF_ROUNDS; i++) {
			warrior2DamageReceived[i] = makeIntVar(
					"warrior 2 damage received for round" + (i + 1),
					HOBGOBLIN_DMG);
			model.addVariable(warrior2DamageReceived[i]);
		}

		// Tableau de variable représentant les dommages reçus par l'hobgoblin 1
		for (int i = 0; i < NB_OF_ROUNDS; i++) {
			hobgoblin1DamageReceived[i] = makeIntVar(
					"hobgoblin 1 damage received for round" + (i + 1),
					WARRIOR_DMG);
			model.addVariable(hobgoblin1DamageReceived[i]);
		}

		// Tableau de variable représentant les dommages reçus par l'hobgoblin 2
		for (int i = 0; i < NB_OF_ROUNDS; i++) {
			hobgoblin2DamageReceived[i] = makeIntVar(
					"hobgoblin 1 damage received for round" + (i + 1),
					WARRIOR_DMG);
			model.addVariable(hobgoblin2DamageReceived[i]);
		}

		// Tableau représentant les attaques du guerrier 1
		for (int i = 0; i < NB_OF_ROUNDS; i++) {
			warrior1DamageDone[i] = makeIntVar(
					"warrior 1 target of damage done for round" + (i + 1),
					WARRIOR_TARGET);
			model.addVariable(warrior1DamageDone[i]);
		}

		// Tableau représentant les attaques du guerrier 2
		for (int i = 0; i < NB_OF_ROUNDS; i++) {
			warrior2DamageDone[i] = makeIntVar(
					"warrior 2 target of damage done for round" + (i + 1),
					WARRIOR_TARGET);
			model.addVariable(warrior2DamageDone[i]);
		}

		// Tableau représentant les attaques du hobgoblin 1
		for (int i = 0; i < NB_OF_ROUNDS; i++) {
			hobgoblin1DamageDone[i] = makeIntVar(
					"Hobgoblin 1 target of damage done for round" + (i + 1),
					HOBGOBLIN_TARGET);
			model.addVariable(hobgoblin1DamageDone[i]);
		}

		// Tableau représentant les attaques du hobgoblin 1
		for (int i = 0; i < NB_OF_ROUNDS; i++) {
			hobgoblin2DamageDone[i] = makeIntVar(
					"Hobgoblin 2 target of damage done for round" + (i + 1),
					HOBGOBLIN_TARGET);
			model.addVariable(hobgoblin2DamageDone[i]);
		}

		// Contraintes qui vérifie si le guerrier est vivant après le round i
		for (int i = 0; i < NB_OF_ROUNDS; i++) {
			IntegerVariable[] tempWarrior1Arrays = Arrays.copyOf(
					warrior1DamageReceived, i);
			IntegerVariable[] tempWarrior2Arrays = Arrays.copyOf(
					warrior2DamageReceived, i);
			IntegerVariable[] tempHobgoblin1Arrays = Arrays.copyOf(
					hobgoblin1DamageReceived, i);
			IntegerVariable[] tempHobgoblin2Arrays = Arrays.copyOf(
					hobgoblin2DamageReceived, i);

			Constraint isWarrior1Dead = gt(sum(tempWarrior1Arrays),
					WARRIOR_MAX_HP);
			Constraint isWarrior2Dead = gt(sum(tempWarrior2Arrays),
					WARRIOR_MAX_HP);
			Constraint isHobgoblin1Dead = gt(sum(tempHobgoblin1Arrays),
					HOBGOBLIN_MAX_HP);
			Constraint isHobgoblin2Dead = gt(sum(tempHobgoblin2Arrays),
					HOBGOBLIN_MAX_HP);

			// dommage reçu par l'hobgoblin 1 (3)
			model.addConstraint(ifThenElse(
					and(isWarrior1Dead, isWarrior2Dead),
					eq(hobgoblin1DamageReceived[i], 0),
					ifThenElse(
							isHobgoblin1Dead,
							eq(hobgoblin1DamageReceived[i], 0),
							ifThenElse(
									and(eq(warrior1DamageDone[i], 3), eq(warrior2DamageDone[i], 3)),
									eq(hobgoblin1DamageReceived[i], 92),
									ifThenElse(
											or(and(eq(warrior1DamageDone[i], 3),
													eq(warrior2DamageDone[i], 4)),
													and(eq(warrior1DamageDone[i],
															4),
															eq(warrior2DamageDone[i],
																	3))),
											eq(hobgoblin1DamageReceived[i], 46),
											eq(hobgoblin1DamageReceived[i], 0))))));

			// dommage reçu par l'hobgoblin 2 (4)
			model.addConstraint(ifThenElse(
					and(isWarrior1Dead, isWarrior2Dead),
					eq(hobgoblin2DamageReceived[i], 0),
					ifThenElse(
							isHobgoblin2Dead,
							eq(hobgoblin2DamageReceived[i], 0),
							ifThenElse(
									and(eq(warrior1DamageDone[i], 4),
											eq(warrior2DamageDone[i], 4)),
									eq(hobgoblin2DamageReceived[i], 92),
									ifThenElse(
											or(and(eq(warrior1DamageDone[i], 3),
													eq(warrior2DamageDone[i], 4)),
													and(eq(warrior1DamageDone[i],
															4),
															eq(warrior2DamageDone[i],
																	3))),
											eq(hobgoblin2DamageReceived[i], 46),
											eq(hobgoblin2DamageReceived[i], 0))))));

			// dommage reçu par le guerrier 1 (1)
			model.addConstraint(ifThenElse(
					and(isHobgoblin1Dead, isHobgoblin2Dead),
					eq(warrior1DamageReceived[i], 0),
					ifThenElse(
							isWarrior1Dead,
							eq(warrior1DamageReceived[i], 0),
							ifThenElse(
									and(eq(hobgoblin1DamageDone[i], 1),
											eq(hobgoblin2DamageDone[i], 1)),
									eq(warrior1DamageReceived[i], 26),
									ifThenElse(
											or(and(eq(hobgoblin1DamageDone[i],
													1),
													eq(hobgoblin2DamageDone[i],
															2)),
													and(eq(hobgoblin1DamageDone[i],
															2),
															eq(hobgoblin2DamageDone[i],
																	1))),
											eq(warrior1DamageReceived[i], 13),
											eq(warrior1DamageReceived[i], 0))))));

			// dommage reçu par le guerrier 2 (2)
			model.addConstraint(ifThenElse(
					and(isHobgoblin1Dead, isHobgoblin2Dead),
					eq(warrior2DamageReceived[i], 0),
					ifThenElse(
							isWarrior2Dead,
							eq(warrior2DamageReceived[i], 0),
							ifThenElse(
									and(eq(hobgoblin1DamageDone[i], 2),
											eq(hobgoblin2DamageDone[i], 2)),
									eq(warrior2DamageReceived[i], 26),
									ifThenElse(
											or(and(eq(hobgoblin1DamageDone[i],
													1),
													eq(hobgoblin2DamageDone[i],
															2)),
													and(eq(hobgoblin1DamageDone[i],
															2),
															eq(hobgoblin2DamageDone[i],
																	1))),
											eq(warrior2DamageReceived[i], 13),
											eq(warrior2DamageReceived[i], 0))))));
		}
		
		// Variable représentant la vie du guerrier 1
		warrior1Hp = makeIntVar("warrior 1 HP", -5000, WARRIOR_MAX_HP,
				Options.V_OBJECTIVE);
		model.addVariable(warrior1Hp);
		model.addConstraint(eq(warrior1Hp,
				minus(WARRIOR_MAX_HP, sum(warrior1DamageReceived))));

		// Variable représentant la vie du guerrier 2
		warrior2Hp = makeIntVar("warrior 2 HP", -5000, WARRIOR_MAX_HP,
				Options.V_OBJECTIVE);
		model.addVariable(warrior2Hp);
		model.addConstraint(eq(warrior2Hp,
				minus(WARRIOR_MAX_HP, sum(warrior2DamageReceived))));

		model.addConstraint(or(gt(warrior1Hp, 0), gt(warrior1Hp, 0)));
		
		
		// Variable représentant la vie de l'hobgoblin
		hobgoblin1Hp = makeIntVar("hobgoblin 1 HP", -6300, HOBGOBLIN_MAX_HP);
		model.addVariable(hobgoblin1Hp);
		model.addConstraint(eq(hobgoblin1Hp,
				minus(HOBGOBLIN_MAX_HP, sum(hobgoblin1DamageReceived))));

		// Variable représentant la vie de l'hobgoblin
		hobgoblin2Hp = makeIntVar("hobgoblin 2 HP", -6300, HOBGOBLIN_MAX_HP);
		model.addVariable(hobgoblin2Hp);
		model.addConstraint(eq(hobgoblin2Hp,
				minus(HOBGOBLIN_MAX_HP, sum(hobgoblin2DamageReceived))));
	}

	public void solveScenario() {
		solveur = new CPSolver();
		// Lecture du modele par le solveur
		solveur.read(model);
		solveur.setVarIntSelector(new StaticVarOrder(solveur, solveur
				.getVar(prepareHeuristic())));
		if (solveur.maximize(true)) {
			System.out.println("Le guerrier 1 a "
					+ solveur.getVar(warrior1Hp).getVal() + " point de vie");
			System.out.println("Le guerrier 2 a "
					+ solveur.getVar(warrior2Hp).getVal() + " point de vie");
			System.out.println("L'hobgoblin 1 a "
					+ solveur.getVar(hobgoblin1Hp).getVal() + " point de vie");
			System.out.println("L'hobgoblin 2 a "
					+ solveur.getVar(hobgoblin2Hp).getVal() + " point de vie");
			System.out.println("--------------------------------");
			for (int i = 0; i < NB_OF_ROUNDS; i++) {
				System.out.println("ROUND " + (i + 1));
				System.out.println("--------");
				System.out.println("Guerrier 1 : -"
						+ solveur.getVar(warrior1DamageReceived[i]).getVal()
						+ " hp");
				System.out.println("Guerrier 2 : -"
						+ solveur.getVar(warrior2DamageReceived[i]).getVal()
						+ " hp");
				System.out.println("Hobgoblin 1 : -"
						+ solveur.getVar(hobgoblin1DamageReceived[i]).getVal()
						+ " hp");
				System.out.println("Hobgoblin 2 : -"
						+ solveur.getVar(hobgoblin2DamageReceived[i]).getVal()
						+ " hp");
				System.out.println("");
			}

		} else {
			System.out.println("Aucune solution trouvee.");
		}
		System.out.println("--------------------------------");
		System.out.println("Probleme resolu en " + solveur.getTimeCount()
				+ " millisecondes.");
		System.out.println("Probleme resolu avec "
				+ solveur.getBackTrackCount() + " retours arrieres.");
	}

	private IntegerVariable[] prepareHeuristic() {
		IntegerVariable[] instantiationOrder = new IntegerVariable[80];
		instantiationOrder[0] = hobgoblin1DamageDone[0];
		instantiationOrder[1] = hobgoblin2DamageDone[0];
		instantiationOrder[2] = warrior1DamageDone[0];
		instantiationOrder[3] = warrior2DamageDone[0];
		instantiationOrder[4] = hobgoblin1DamageReceived[0];
		instantiationOrder[5] = hobgoblin2DamageReceived[0];
		instantiationOrder[6] = warrior1DamageReceived[0];
		instantiationOrder[7] = warrior2DamageReceived[0];

		instantiationOrder[8] = hobgoblin1DamageDone[1];
		instantiationOrder[9] = hobgoblin2DamageDone[1];
		instantiationOrder[10] = warrior1DamageDone[1];
		instantiationOrder[11] = warrior2DamageDone[1];
		instantiationOrder[12] = hobgoblin1DamageReceived[1];
		instantiationOrder[13] = hobgoblin2DamageReceived[1];
		instantiationOrder[14] = warrior1DamageReceived[1];
		instantiationOrder[15] = warrior2DamageReceived[1];

		instantiationOrder[16] = hobgoblin1DamageDone[2];
		instantiationOrder[17] = hobgoblin2DamageDone[2];
		instantiationOrder[18] = warrior1DamageDone[2];
		instantiationOrder[19] = warrior2DamageDone[2];
		instantiationOrder[20] = hobgoblin1DamageReceived[2];
		instantiationOrder[21] = hobgoblin2DamageReceived[2];
		instantiationOrder[22] = warrior1DamageReceived[2];
		instantiationOrder[23] = warrior2DamageReceived[2];

		instantiationOrder[24] = hobgoblin1DamageDone[3];
		instantiationOrder[25] = hobgoblin2DamageDone[3];
		instantiationOrder[26] = warrior1DamageDone[3];
		instantiationOrder[27] = warrior2DamageDone[3];
		instantiationOrder[28] = hobgoblin1DamageReceived[3];
		instantiationOrder[29] = hobgoblin2DamageReceived[3];
		instantiationOrder[30] = warrior1DamageReceived[3];
		instantiationOrder[31] = warrior2DamageReceived[3];

		instantiationOrder[32] = hobgoblin1DamageDone[4];
		instantiationOrder[33] = hobgoblin2DamageDone[4];
		instantiationOrder[34] = warrior1DamageDone[4];
		instantiationOrder[35] = warrior2DamageDone[4];
		instantiationOrder[36] = hobgoblin1DamageReceived[4];
		instantiationOrder[37] = hobgoblin2DamageReceived[4];
		instantiationOrder[38] = warrior1DamageReceived[4];
		instantiationOrder[39] = warrior2DamageReceived[4];

		instantiationOrder[40] = hobgoblin1DamageDone[5];
		instantiationOrder[41] = hobgoblin2DamageDone[5];
		instantiationOrder[42] = warrior1DamageDone[5];
		instantiationOrder[43] = warrior2DamageDone[5];
		instantiationOrder[44] = hobgoblin1DamageReceived[5];
		instantiationOrder[45] = hobgoblin2DamageReceived[5];
		instantiationOrder[46] = warrior1DamageReceived[5];
		instantiationOrder[47] = warrior2DamageReceived[5];

		instantiationOrder[48] = hobgoblin1DamageDone[6];
		instantiationOrder[49] = hobgoblin2DamageDone[6];
		instantiationOrder[50] = warrior1DamageDone[6];
		instantiationOrder[51] = warrior2DamageDone[6];
		instantiationOrder[52] = hobgoblin1DamageReceived[6];
		instantiationOrder[53] = hobgoblin2DamageReceived[6];
		instantiationOrder[54] = warrior1DamageReceived[6];
		instantiationOrder[55] = warrior2DamageReceived[6];

		instantiationOrder[56] = hobgoblin1DamageDone[7];
		instantiationOrder[57] = hobgoblin2DamageDone[7];
		instantiationOrder[58] = warrior1DamageDone[7];
		instantiationOrder[59] = warrior2DamageDone[7];
		instantiationOrder[60] = hobgoblin1DamageReceived[7];
		instantiationOrder[61] = hobgoblin2DamageReceived[7];
		instantiationOrder[62] = warrior1DamageReceived[7];
		instantiationOrder[63] = warrior2DamageReceived[7];

		instantiationOrder[64] = hobgoblin1DamageDone[8];
		instantiationOrder[65] = hobgoblin2DamageDone[8];
		instantiationOrder[66] = warrior1DamageDone[8];
		instantiationOrder[67] = warrior2DamageDone[8];
		instantiationOrder[68] = hobgoblin1DamageReceived[8];
		instantiationOrder[69] = hobgoblin2DamageReceived[8];
		instantiationOrder[70] = warrior1DamageReceived[8];
		instantiationOrder[71] = warrior2DamageReceived[8];

		instantiationOrder[72] = hobgoblin1DamageDone[9];
		instantiationOrder[73] = hobgoblin2DamageDone[9];
		instantiationOrder[74] = warrior1DamageDone[9];
		instantiationOrder[75] = warrior2DamageDone[9];
		instantiationOrder[76] = hobgoblin1DamageReceived[9];
		instantiationOrder[77] = hobgoblin2DamageReceived[9];
		instantiationOrder[78] = warrior1DamageReceived[9];
		instantiationOrder[79] = warrior2DamageReceived[9];
		return instantiationOrder;

	}

}
