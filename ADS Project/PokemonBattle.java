import java.util.*;

/**
 * Simple console Pokemon-style battle game (3 vs 3)
 * - Choose 3 from a list of 20 Pokémon
 * - Computer picks 3 randomly
 * - Each Pokemon has 3 moves; damage depends on type matchups
 *
 * Run: javac PokemonBattle.java && java PokemonBattle
 */
public class PokemonBattle {
    public static void main(String[] args) {
        new Game().start();
    }
}

/* ---------- Data models ---------- */

enum Type {
    FIRE, WATER, GRASS, NORMAL
}

class Move {
    String name;
    Type type;
    int power; // base power

    Move(String name, Type type, int power) {
        this.name = name;
        this.type = type;
        this.power = power;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, Power:%d)", name, type, power);
    }
}

class Pokemon {
    String name;
    Type type;
    int maxHp;
    int hp;
    int attack; // used minimally in damage formula
    List<Move> moves;

    Pokemon(String name, Type type, int maxHp, int attack, List<Move> moves) {
        this.name = name;
        this.type = type;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.moves = moves;
    }

    boolean isFainted() {
        return hp <= 0;
    }

    void healToFull() {
        this.hp = maxHp;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] HP:%d/%d Atk:%d", name, type, hp, maxHp, attack);
    }
}

/* ---------- Game logic ---------- */

class Game {
    Scanner scanner = new Scanner(System.in);
    Random random = new Random();
    Map<Type, Map<Type, Double>> effectiveness;

    List<Pokemon> pokedex = new ArrayList<>();

    Game() {
        setupEffectiveness();
        createPokedex();
    }

    void start() {
        System.out.println("=== Welcome to Mini Pokémon Battle (3 vs 3) ===\n");
        List<Pokemon> playerChoices = new ArrayList<>(pokedex);
        // Show pokedex
        printPokedex(playerChoices);

        // Player picks 3
        List<Pokemon> playerTeam = pickTeam(playerChoices, 3, true);

        // Computer picks 3 random from remaining
        List<Pokemon> remaining = new ArrayList<>(pokedex);
        remaining.removeAll(playerTeam);
        Collections.shuffle(remaining);
        List<Pokemon> cpuTeam = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Pokemon p = copyPokemon(remaining.get(i));
            cpuTeam.add(p);
        }

        System.out.println("\nYour team:");
        for (Pokemon p : playerTeam) System.out.println(" - " + p);
        System.out.println("\nComputer's team:");
        for (Pokemon p : cpuTeam) System.out.println(" - " + p.name + " [" + p.type + "]");

        // Reset HP (we created copies for CPU but player's chosen ones must be copied too)
        List<Pokemon> playerTeamCopies = new ArrayList<>();
        for (Pokemon p : playerTeam) playerTeamCopies.add(copyPokemon(p));

        // Start battle
        battleLoop(playerTeamCopies, cpuTeam);
        System.out.println("=== Battle ended ===");
    }

    void battleLoop(List<Pokemon> playerTeam, List<Pokemon> cpuTeam) {
        int pIndex = 0;
        int cIndex = 0;

        Pokemon pActive = playerTeam.get(pIndex);
        Pokemon cActive = cpuTeam.get(cIndex);

        System.out.println("\nBattle start!");
        while (true) {
            if (pActive.isFainted()) {
                pIndex++;
                if (pIndex >= playerTeam.size()) {
                    System.out.println("\nAll your Pokémon fainted. You lose!");
                    break;
                }
                pActive = playerTeam.get(pIndex);
                System.out.println("\nYou send out " + pActive.name + "!");
            }

            if (cActive.isFainted()) {
                cIndex++;
                if (cIndex >= cpuTeam.size()) {
                    System.out.println("\nAll opponent Pokémon fainted. You win!");
                    break;
                }
                cActive = cpuTeam.get(cIndex);
                System.out.println("\nOpponent sends out " + cActive.name + "!");
            }

            // Show status
            System.out.println("\nYour active: " + pActive);
            System.out.println("Opponent active: " + cActive);

            // Player's turn: choose move
            int moveIndex = choosePlayerMove(pActive);
            Move chosen = pActive.moves.get(moveIndex);
            performMove(pActive, cActive, chosen, true);
            if (cActive.isFainted()) {
                System.out.println(cActive.name + " fainted!");
                continue; // loop will handle switching
            }

            // CPU turn: random move
            int cpuMoveIndex = random.nextInt(cActive.moves.size());
            Move cpuMove = cActive.moves.get(cpuMoveIndex);
            System.out.println("\nOpponent uses " + cpuMove.name + "!");
            performMove(cActive, pActive, cpuMove, false);
            if (pActive.isFainted()) {
                System.out.println(pActive.name + " fainted!");
            }
        }
    }

    int choosePlayerMove(Pokemon p) {
        System.out.println("\nChoose a move:");
        for (int i = 0; i < p.moves.size(); i++) {
            System.out.println((i + 1) + ". " + p.moves.get(i));
        }
        System.out.print("Enter move number (1-" + p.moves.size() + "): ");
        while (true) {
            String in = scanner.nextLine();
            try {
                int choice = Integer.parseInt(in.trim());
                if (choice >= 1 && choice <= p.moves.size()) return choice - 1;
            } catch (NumberFormatException ignored) {}
            System.out.print("Invalid. Enter move number: ");
        }
    }

    void performMove(Pokemon attacker, Pokemon defender, Move move, boolean isPlayer) {
        double multiplier = getEffectiveness(move.type, defender.type);
        // Simple damage formula: damage = ((attack + move.power) * multiplier) / 4
        int raw = attacker.attack + move.power;
        int damage = (int) Math.max(1, Math.round(raw * multiplier / 4.0));
        defender.hp -= damage;
        if (defender.hp < 0) defender.hp = 0;

        String effText = "";
        if (multiplier >= 1.99) effText = " It's super effective!";
        else if (multiplier <= 0.51) effText = " It's not very effective.";

        if (isPlayer)
            System.out.printf("\nYou used %s! It dealt %d damage.%s\n", move.name, damage, effText);
        else
            System.out.printf("%s used %s! It dealt %d damage.%s\n", attacker.name, move.name, damage, effText);

        System.out.printf("%s HP is now %d/%d\n", defender.name, defender.hp, defender.maxHp);
    }

    double getEffectiveness(Type attackType, Type defendType) {
        return effectiveness.getOrDefault(attackType, Collections.emptyMap())
                            .getOrDefault(defendType, 1.0);
    }

    void setupEffectiveness() {
        effectiveness = new HashMap<>();
        // Base all to neutral
        for (Type t : Type.values()) {
            Map<Type, Double> m = new HashMap<>();
            for (Type d : Type.values()) m.put(d, 1.0);
            effectiveness.put(t, m);
        }
        // Fire > Grass, Fire < Water
        effectiveness.get(Type.FIRE).put(Type.GRASS, 2.0);
        effectiveness.get(Type.FIRE).put(Type.WATER, 0.5);

        // Water > Fire, Water < Grass
        effectiveness.get(Type.WATER).put(Type.FIRE, 2.0);
        effectiveness.get(Type.WATER).put(Type.GRASS, 0.5);

        // Grass > Water, Grass < Fire
        effectiveness.get(Type.GRASS).put(Type.WATER, 2.0);
        effectiveness.get(Type.GRASS).put(Type.FIRE, 0.5);

        // Normal is neutral vs all (already 1.0)
    }

    void printPokedex(List<Pokemon> list) {
        System.out.println("Choose 3 Pokémon from the list below:");
        for (int i = 0; i < list.size(); i++) {
            Pokemon p = list.get(i);
            System.out.printf("%2d) %s [%s] HP:%d Atk:%d\n", i+1, p.name, p.type, p.maxHp, p.attack);
        }
    }

    List<Pokemon> pickTeam(List<Pokemon> options, int howMany, boolean isPlayer) {
        List<Pokemon> team = new ArrayList<>();
        if (isPlayer) {
            while (team.size() < howMany) {
                System.out.printf("\nPick Pokémon #%d by number: ", team.size() + 1);
                String in = scanner.nextLine();
                try {
                    int idx = Integer.parseInt(in.trim()) - 1;
                    if (idx < 0 || idx >= options.size()) {
                        System.out.println("Index out of range.");
                        continue;
                    }
                    Pokemon chosen = options.get(idx);
                    if (team.contains(chosen)) {
                        System.out.println("You already picked that Pokémon.");
                        continue;
                    }
                    team.add(chosen);
                    System.out.println("Added " + chosen.name + " to your team.");
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }
        } else {
            // not used - CPU picks elsewhere
        }
        return team;
    }

    Pokemon copyPokemon(Pokemon p) {
        List<Move> newMoves = new ArrayList<>();
        for (Move m : p.moves) newMoves.add(new Move(m.name, m.type, m.power));
        return new Pokemon(p.name, p.type, p.maxHp, p.attack, newMoves);
    }

    /* ---------- Pokedex creation (20 Pokémon) ---------- */
    void createPokedex() {
        // We'll create simple move sets (3 moves) focused on type and two neutral moves
        // For variety keep HP and Attack numbers modest
        pokedex.add(new Pokemon("Charmander", Type.FIRE, 39, 52, movesOf(Type.FIRE)));
        pokedex.add(new Pokemon("Vulpix", Type.FIRE, 38, 41, movesOf(Type.FIRE)));
        pokedex.add(new Pokemon("Growlithe", Type.FIRE, 55, 70, movesOf(Type.FIRE)));

        pokedex.add(new Pokemon("Squirtle", Type.WATER, 44, 48, movesOf(Type.WATER)));
        pokedex.add(new Pokemon("Psyduck", Type.WATER, 50, 52, movesOf(Type.WATER)));
        pokedex.add(new Pokemon("Poliwag", Type.WATER, 40, 50, movesOf(Type.WATER)));

        pokedex.add(new Pokemon("Bulbasaur", Type.GRASS, 45, 49, movesOf(Type.GRASS)));
        pokedex.add(new Pokemon("Oddish", Type.GRASS, 45, 50, movesOf(Type.GRASS)));
        pokedex.add(new Pokemon("Bellsprout", Type.GRASS, 50, 75, movesOf(Type.GRASS)));

        pokedex.add(new Pokemon("Pidgey", Type.NORMAL, 40, 45, movesOf(Type.NORMAL)));
        pokedex.add(new Pokemon("Rattata", Type.NORMAL, 30, 56, movesOf(Type.NORMAL)));
        pokedex.add(new Pokemon("Sentret", Type.NORMAL, 35, 46, movesOf(Type.NORMAL)));

        pokedex.add(new Pokemon("Geodude", Type.NORMAL, 40, 80, neutralMoves()));
        pokedex.add(new Pokemon("Zubat", Type.NORMAL, 40, 45, neutralMoves()));
        pokedex.add(new Pokemon("Meowth", Type.NORMAL, 40, 45, neutralMoves()));

        pokedex.add(new Pokemon("Odd-type1", Type.FIRE, 48, 60, movesOf(Type.FIRE)));
        pokedex.add(new Pokemon("Odd-type2", Type.WATER, 42, 56, movesOf(Type.WATER)));
        pokedex.add(new Pokemon("Odd-type3", Type.GRASS, 46, 54, movesOf(Type.GRASS)));
        pokedex.add(new Pokemon("Eevee", Type.NORMAL, 55, 55, neutralMoves()));
        pokedex.add(new Pokemon("Pikachu", Type.NORMAL, 35, 55, neutralMoves()));
    }

    // returns three moves: one of same type (strong), two neutral "Tackle"-like moves
    List<Move> movesOf(Type t) {
        List<Move> m = new ArrayList<>();
        m.add(new Move(t.name() + " Strike", t, 50)); // main type move
        m.add(new Move("Tackle", Type.NORMAL, 40));
        m.add(new Move("Quick Hit", Type.NORMAL, 30));
        return m;
    }

    List<Move> neutralMoves() {
        List<Move> m = new ArrayList<>();
        m.add(new Move("Slash", Type.NORMAL, 50));
        m.add(new Move("Tackle", Type.NORMAL, 40));
        m.add(new Move("Bite", Type.NORMAL, 35));
        return m;
    }
}