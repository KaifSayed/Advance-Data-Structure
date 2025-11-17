import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.File;
import java.awt.image.BufferedImage;

/**
 * PokemonBattleGUI.java
 *
 * Swing GUI version (4-quadrant layout):
 * - TL: Opponent (sprite + name + HP)
 * - TR: Log (scrollable) above Dialogue area
 * - BL: Player (sprite + name + HP)
 * - BR: Controls (moves + switch) and small team status
 *
 * Run: javac PokemonBattleGUI.java && java PokemonBattleGUI
 */
public class PokemonBattleGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BattleFrame().setVisible(true));
    }
}

/* ------------------- Data models ------------------- */

enum PokeType { FIRE, WATER, GRASS, NORMAL }

class Move {
    String name;
    PokeType type;
    int power;
    Move(String name, PokeType type, int power) { this.name = name; this.type = type; this.power = power; }
    @Override public String toString() { return String.format("%s (%s P:%d)", name, type, power); }
}

class Pokemon {
    String name;
    PokeType type;
    int maxHp;
    int hp;
    int attack;
    java.util.List<Move> moves;
    ImageIcon sprite;

    Pokemon(String name, PokeType type, int maxHp, int attack, java.util.List<Move> moves) {
        this.name = name; this.type = type; this.maxHp = maxHp; this.hp = maxHp; this.attack = attack; this.moves = moves;
        this.sprite = null;
    }

    boolean isFainted() { return hp <= 0; }
    void heal() { hp = maxHp; }
    @Override public String toString() { return String.format("%s [%s] HP:%d/%d Atk:%d", name, type, hp, maxHp, attack); }
}

/* ------------------- Main GUI Frame ------------------- */

class BattleFrame extends JFrame {
    // Data
    java.util.List<Pokemon> pokedex = new ArrayList<>();
    java.util.List<Pokemon> playerTeam = new ArrayList<>();
    java.util.List<Pokemon> cpuTeam = new ArrayList<>();
    int playerIndex = 0;
    int cpuIndex = 0;

    // Type effectiveness table
    Map<PokeType, Map<PokeType, Double>> eff = new HashMap<>();

    // UI components
    JLabel lblPlayerName = new JLabel("", SwingConstants.CENTER);
    JLabel lblCpuName = new JLabel("", SwingConstants.CENTER);
    JLabel lblPlayerSprite = new JLabel();
    JLabel lblCpuSprite = new JLabel();
    JProgressBar pbPlayer = new JProgressBar();
    JProgressBar pbCpu = new JProgressBar();
    JButton[] moveButtons = new JButton[3];
    JButton btnSwitch = new JButton("Switch (uses turn)");
    JTextArea logArea = new JTextArea();
    JTextArea dialogArea = new JTextArea(3, 10); // visible in TR quadrant
    JTextArea teamStatusArea = new JTextArea(6, 12); // in BR for small team status

    Random rnd = new Random();

    // preferred HP bar size (keeps both equal)
    final Dimension HP_BAR_SIZE = new Dimension(320, 22);
    final Dimension SPRITE_SIZE = new Dimension(260, 200);

    BattleFrame() {
        setTitle("Mini Pokémon Battle - GUI (Swing)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setupEffectiveness();
        initPokedex();
        loadSprites();

        buildUI();
        askPlayerPickTeam();
    }

    void buildUI() {
        JPanel root = new JPanel(new GridLayout(2,2,8,8)); // 4 quadrants
        root.setBorder(new EmptyBorder(8,8,8,8));

        // --------- TL: Opponent (sprite + name + hp) ----------
        JPanel tl = new JPanel();
        tl.setLayout(new BorderLayout(6,6));
        tl.setBorder(BorderFactory.createTitledBorder("Opponent"));
        lblCpuName.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblCpuName.setPreferredSize(new Dimension(100, 30));
        JPanel cpuTop = new JPanel(new BorderLayout());
        cpuTop.add(lblCpuName, BorderLayout.NORTH);

        JLabel cpuSpriteHolder = new JLabel();
        cpuSpriteHolder.setHorizontalAlignment(SwingConstants.CENTER);
        cpuSpriteHolder.add(lblCpuSprite);
        JPanel spriteWrap = new JPanel(new GridBagLayout());
        spriteWrap.add(lblCpuSprite);
        spriteWrap.setPreferredSize(SPRITE_SIZE);

        pbCpu.setStringPainted(true);
        pbCpu.setPreferredSize(HP_BAR_SIZE);
        pbCpu.setMinimumSize(HP_BAR_SIZE);
        pbCpu.setMaximumSize(HP_BAR_SIZE);

        tl.add(spriteWrap, BorderLayout.CENTER);
        JPanel cpuBottom = new JPanel();
        cpuBottom.setLayout(new BoxLayout(cpuBottom, BoxLayout.Y_AXIS));
        cpuBottom.add(Box.createVerticalStrut(6));
        cpuBottom.add(pbCpu);
        cpuBottom.add(Box.createVerticalStrut(6));
        tl.add(cpuBottom, BorderLayout.SOUTH);

        // --------- TR: Log (scrollable) above Dialogue ----------
        JPanel tr = new JPanel();
        tr.setLayout(new BorderLayout(6,6));
        tr.setBorder(BorderFactory.createTitledBorder("Battle Log & Dialogue"));
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(350, 300));

        dialogArea.setEditable(false);
        dialogArea.setLineWrap(true);
        dialogArea.setWrapStyleWord(true);
        dialogArea.setFont(dialogArea.getFont().deriveFont(Font.PLAIN, 13f));
        dialogArea.setBorder(BorderFactory.createTitledBorder("Dialogue"));
        JScrollPane dialogScroll = new JScrollPane(dialogArea);
        dialogScroll.setPreferredSize(new Dimension(350, 120));

        tr.add(logScroll, BorderLayout.CENTER);
        tr.add(dialogScroll, BorderLayout.SOUTH);

        // --------- BL: Player (sprite + name + hp) ----------
        JPanel bl = new JPanel();
        bl.setLayout(new BorderLayout(6,6));
        bl.setBorder(BorderFactory.createTitledBorder("You"));
        lblPlayerName.setFont(new Font("SansSerif", Font.BOLD, 16));
        JPanel playerTop = new JPanel(new BorderLayout());
        playerTop.add(lblPlayerName, BorderLayout.NORTH);

        JPanel pSpriteWrap = new JPanel(new GridBagLayout());
        pSpriteWrap.add(lblPlayerSprite);
        pSpriteWrap.setPreferredSize(SPRITE_SIZE);

        pbPlayer.setStringPainted(true);
        pbPlayer.setPreferredSize(HP_BAR_SIZE);
        pbPlayer.setMinimumSize(HP_BAR_SIZE);
        pbPlayer.setMaximumSize(HP_BAR_SIZE);

        bl.add(pSpriteWrap, BorderLayout.CENTER);
        JPanel playerBottom = new JPanel();
        playerBottom.setLayout(new BoxLayout(playerBottom, BoxLayout.Y_AXIS));
        playerBottom.add(Box.createVerticalStrut(6));
        playerBottom.add(pbPlayer);
        playerBottom.add(Box.createVerticalStrut(6));
        bl.add(playerBottom, BorderLayout.SOUTH);

        // --------- BR: Controls (moves + switch) + small team status ----------
        JPanel br = new JPanel();
        br.setLayout(new BorderLayout(6,6));
        br.setBorder(BorderFactory.createTitledBorder("Controls"));
        JPanel movesPanel = new JPanel(new GridLayout(2,2,6,6));
        for (int i=0;i<3;i++) {
            moveButtons[i] = new JButton("Move " + (i+1));
            int idx = i;
            moveButtons[i].addActionListener(e -> playerUseMove(idx));
            movesPanel.add(moveButtons[i]);
        }
        btnSwitch.addActionListener(e -> doSwitch());
        movesPanel.add(btnSwitch);

        // Team status small area
        teamStatusArea.setEditable(false);
        teamStatusArea.setFont(teamStatusArea.getFont().deriveFont(Font.PLAIN, 12f));
        teamStatusArea.setBorder(BorderFactory.createTitledBorder("Team Status"));
        JScrollPane teamScroll = new JScrollPane(teamStatusArea);
        teamScroll.setPreferredSize(new Dimension(200,150));

        br.add(movesPanel, BorderLayout.CENTER);
        br.add(teamScroll, BorderLayout.SOUTH);

        // add quadrants to root in order: TL, TR, BL, BR
        root.add(tl);
        root.add(tr);
        root.add(bl);
        root.add(br);

        setContentPane(root);
    }

    // Ask player to pick team (dialog with checkbox list)
    void askPlayerPickTeam() {
        JDialog dlg = new JDialog(this, "Pick 3 Pokémon", true);
        dlg.setSize(520, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(8,8));

        JLabel instr = new JLabel("Select exactly 3 Pokémon using the checkboxes below and press Confirm");
        instr.setBorder(new EmptyBorder(6,6,6,6));
        dlg.add(instr, BorderLayout.NORTH);

        // Panel with checkboxes
        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(new GridLayout(0, 1, 4, 4));
        JCheckBox[] boxes = new JCheckBox[pokedex.size()];
        for (int i = 0; i < pokedex.size(); i++) {
            Pokemon p = pokedex.get(i);
            boxes[i] = new JCheckBox(p.name + " [" + p.type + "] HP:" + p.maxHp + " Atk:" + p.attack);
            boxPanel.add(boxes[i]);
        }
        JScrollPane sp = new JScrollPane(boxPanel);
        dlg.add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(4,4));
        JLabel countLabel = new JLabel("Selected: 0");
        JButton btnConfirm = new JButton("Confirm");
        JButton btnCancel = new JButton("Cancel");
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnConfirm);
        btnPanel.add(btnCancel);
        bottom.add(countLabel, BorderLayout.WEST);
        bottom.add(btnPanel, BorderLayout.EAST);
        dlg.add(bottom, BorderLayout.SOUTH);

        // Update selected count when boxes change
        ItemListener updateCount = e -> {
            int cnt = 0;
            for (JCheckBox cb : boxes) if (cb.isSelected()) cnt++;
            countLabel.setText("Selected: " + cnt);
        };
        for (JCheckBox cb : boxes) cb.addItemListener(updateCount);

        btnConfirm.addActionListener(e -> {
            java.util.List<Integer> sel = new ArrayList<>();
            for (int i = 0; i < boxes.length; i++) if (boxes[i].isSelected()) sel.add(i);
            if (sel.size() != 3) {
                JOptionPane.showMessageDialog(dlg, "Please select exactly three Pokémon.", "Selection error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            playerTeam.clear();
            for (int i : sel) playerTeam.add(copyPokemon(pokedex.get(i)));
            dlg.dispose();
            startMatch();
        });
        btnCancel.addActionListener(e -> System.exit(0));

        dlg.setVisible(true);
    }

    void startMatch() {
        // CPU picks 3 random from remaining
        java.util.List<Pokemon> pool = new ArrayList<>(pokedex);
        Set<String> pickedNames = new HashSet<>();
        for (Pokemon p : playerTeam) pickedNames.add(p.name);
        pool.removeIf(p -> pickedNames.contains(p.name));
        Collections.shuffle(pool);
        cpuTeam.clear();
        for (int i=0;i<3;i++) cpuTeam.add(copyPokemon(pool.get(i)));

        playerIndex = 0; cpuIndex = 0;
        for (Pokemon p: playerTeam) p.heal();
        for (Pokemon p: cpuTeam) p.heal();
        updateUIAll();
        log("Battle begins! You vs Opponent.");
        dialog("A wild " + cpuTeam.get(cpuIndex).name + " appears!");
        updateTeamStatus();
    }

    void updateUIAll() {
        Pokemon player = playerTeam.get(playerIndex);
        Pokemon cpu = cpuTeam.get(cpuIndex);

        lblPlayerName.setText(player.name + " [" + player.type + "]");
        lblPlayerSprite.setIcon(scaledIcon(player.sprite, SPRITE_SIZE.width, SPRITE_SIZE.height));
        pbPlayer.setMaximum(player.maxHp);
        pbPlayer.setValue(player.hp);
        pbPlayer.setString(player.hp + " / " + player.maxHp);
        pbPlayer.setPreferredSize(HP_BAR_SIZE);
        pbPlayer.setMinimumSize(HP_BAR_SIZE);
        pbPlayer.setMaximumSize(HP_BAR_SIZE);

        lblCpuName.setText(cpu.name + " [" + cpu.type + "]");
        lblCpuSprite.setIcon(scaledIcon(cpu.sprite, SPRITE_SIZE.width, SPRITE_SIZE.height));
        pbCpu.setMaximum(cpu.maxHp);
        pbCpu.setValue(cpu.hp);
        pbCpu.setString(cpu.hp + " / " + cpu.maxHp);
        // enforce same size
        pbCpu.setPreferredSize(HP_BAR_SIZE);
        pbCpu.setMinimumSize(HP_BAR_SIZE);
        pbCpu.setMaximumSize(HP_BAR_SIZE);

        // populate move buttons text and enable only if not fainted
        boolean enabled = !player.isFainted();
        for (int i=0;i<3;i++) {
            Move m = player.moves.get(i);
            moveButtons[i].setText("<html><center>" + m.name + "<br/>(" + m.type + " P:" + m.power + ")</center></html>");
            moveButtons[i].setEnabled(enabled);
        }
        btnSwitch.setEnabled(enabled && playerTeam.stream().anyMatch(p -> !p.isFainted() && p != player));
        updateTeamStatus();
        repaint();
    }

    void playerUseMove(int moveIdx) {
        Pokemon player = playerTeam.get(playerIndex);
        Pokemon cpu = cpuTeam.get(cpuIndex);
        if (player.isFainted()) {
            log("Your active Pokémon is fainted - choose a different one.");
            return;
        }
        Move chosen = player.moves.get(moveIdx);
        log("You used " + chosen.name + "!");
        dialog("You used " + chosen.name + "!");
        applyDamage(player, cpu, chosen, true);
        updateUIAll();
        if (cpu.isFainted()) {
            log(cpu.name + " fainted!");
            dialog(cpu.name + " fainted!");
            cpuIndex = advanceIndex(cpuTeam, cpuIndex);
            if (cpuIndex >= cpuTeam.size()) {
                JOptionPane.showMessageDialog(this, "You win! All opponent Pokémon fainted.");
                disableAll();
                return;
            } else {
                log("Opponent sends out " + cpuTeam.get(cpuIndex).name + "!");
                dialog("Opponent sends out " + cpuTeam.get(cpuIndex).name + "!");
                updateUIAll();
            }
            return;
        }

        // CPU turn after short delay
        setAllButtonsEnabled(false);
        new javax.swing.Timer(700, e -> {
            ((javax.swing.Timer)e.getSource()).stop();
            performCpuTurn();
        }).start();
    }

    void performCpuTurn() {
        Pokemon cpu = cpuTeam.get(cpuIndex);
        Pokemon player = playerTeam.get(playerIndex);
        if (cpu.isFainted()) {
            cpuIndex = advanceIndex(cpuTeam, cpuIndex);
            if (cpuIndex >= cpuTeam.size()) {
                JOptionPane.showMessageDialog(this, "You win!");
                disableAll();
                return;
            } else {
                log("Opponent sends out " + cpuTeam.get(cpuIndex).name + "!");
                dialog("Opponent sends out " + cpuTeam.get(cpuIndex).name + "!");
                updateUIAll();
            }
            setAllButtonsEnabled(true);
            return;
        }
        // Improved AI: pick super-effective move if any; else pick highest expected damage (power * effectiveness)
        Move best = null;
        double bestScore = -1;
        for (Move m : cpu.moves) {
            double mul = getEffectiveness(m.type, player.type);
            double score = m.power * mul;
            if (mul > 1.5) score += 20; // prefer super-effective
            if (score > bestScore) { bestScore = score; best = m; }
        }
        log("Opponent uses " + best.name + "!");
        dialog("Opponent uses " + best.name + "!");
        applyDamage(cpu, player, best, false);
        updateUIAll();
        if (player.isFainted()) {
            log(player.name + " fainted!");
            dialog(player.name + " fainted!");
            playerIndex = advanceIndex(playerTeam, playerIndex);
            if (playerIndex >= playerTeam.size()) {
                JOptionPane.showMessageDialog(this, "All your Pokémon fainted. You lose.");
                disableAll();
                return;
            } else {
                log("You send out " + playerTeam.get(playerIndex).name + "!");
                dialog("You send out " + playerTeam.get(playerIndex).name + "!");
                updateUIAll();
            }
        }
        setAllButtonsEnabled(true);
    }

    // Switch button: opens dialog to pick one of other team members (not fainted). Switching consumes player's turn, then CPU moves.
    void doSwitch() {
        java.util.List<String> choices = new ArrayList<>();
        java.util.List<Integer> indices = new ArrayList<>();
        for (int i=0;i<playerTeam.size();i++) {
            if (i==playerIndex) continue;
            Pokemon p = playerTeam.get(i);
            if (!p.isFainted()) { choices.add(p.name + " [" + p.hp + "/" + p.maxHp + "]"); indices.add(i); }
        }
        if (choices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available Pokémon to switch to.");
            return;
        }
        String sel = (String) JOptionPane.showInputDialog(this, "Choose Pokémon to switch to (consumes your turn):",
                "Switch", JOptionPane.PLAIN_MESSAGE, null, choices.toArray(), choices.get(0));
        if (sel == null) return;
        int chosenIndex = indices.get(choices.indexOf(sel));
        playerIndex = chosenIndex;
        log("You switched to " + playerTeam.get(playerIndex).name + ". (This uses your turn)");
        dialog("You switched to " + playerTeam.get(playerIndex).name + ".");
        updateUIAll();
        // CPU immediately moves
        setAllButtonsEnabled(false);
        new javax.swing.Timer(700, e -> {
            ((javax.swing.Timer)e.getSource()).stop();
            performCpuTurn();
        }).start();
    }

    void applyDamage(Pokemon attacker, Pokemon defender, Move move, boolean isPlayer) {
        double mul = getEffectiveness(move.type, defender.type);
        int raw = attacker.attack + move.power;
        int damage = (int)Math.max(1, Math.round(raw * mul / 4.0));
        defender.hp -= damage;
        if (defender.hp < 0) defender.hp = 0;

        String effText = "";
        if (mul >= 1.99) effText = " It's super effective!";
        else if (mul <= 0.51) effText = " It's not very effective.";

        if (isPlayer) {
            log(String.format("You dealt %d damage to %s.%s", damage, defender.name, effText));
            dialog(String.format("You dealt %d to %s.%s", damage, defender.name, effText));
        } else {
            log(String.format("Opponent dealt %d damage to %s.%s", damage, defender.name, effText));
            dialog(String.format("Opponent dealt %d to %s.%s", damage, defender.name, effText));
        }
    }

    void setAllButtonsEnabled(boolean en) {
        for (JButton b : moveButtons) b.setEnabled(en);
        btnSwitch.setEnabled(en && playerTeam.stream().anyMatch(p -> !p.isFainted() && p != playerTeam.get(playerIndex)));
    }

    void disableAll() {
        setAllButtonsEnabled(false);
    }

    int advanceIndex(java.util.List<Pokemon> team, int current) {
        // returns new index if any alive remain, else returns team.size() (meaning none left)
        for (int i = current+1; i < team.size(); i++) if (!team.get(i).isFainted()) return i;
        for (int i = 0; i < current; i++) if (!team.get(i).isFainted()) return i;
        return team.size();
    }

    void log(String s) {
        logArea.append(s + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    void dialog(String s) {
        dialogArea.append(s + "\n");
        dialogArea.setCaretPosition(dialogArea.getDocument().getLength());
    }

    void updateTeamStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("Your team:\n");
        for (int i=0;i<playerTeam.size();i++) {
            Pokemon p = playerTeam.get(i);
            String active = (i==playerIndex) ? " (active)" : "";
            sb.append(String.format("%s%s - %d/%d\n", p.name, active, p.hp, p.maxHp));
        }
        sb.append("\nOpponent:\n");
        for (int i=0;i<cpuTeam.size();i++) {
            Pokemon p = cpuTeam.get(i);
            String active = (i==cpuIndex) ? " (active)" : "";
            sb.append(String.format("%s%s - %d/%d\n", p.name, active, p.hp, p.maxHp));
        }
        teamStatusArea.setText(sb.toString());
    }

    // ----------------- Pokedex and helpers -----------------

    void setupEffectiveness() {
        for (PokeType t : PokeType.values()) {
            Map<PokeType, Double> m = new EnumMap<>(PokeType.class);
            for (PokeType d : PokeType.values()) m.put(d, 1.0);
            eff.put(t, m);
        }
        eff.get(PokeType.FIRE).put(PokeType.GRASS, 2.0);
        eff.get(PokeType.FIRE).put(PokeType.WATER, 0.5);

        eff.get(PokeType.WATER).put(PokeType.FIRE, 2.0);
        eff.get(PokeType.WATER).put(PokeType.GRASS, 0.5);

        eff.get(PokeType.GRASS).put(PokeType.WATER, 2.0);
        eff.get(PokeType.GRASS).put(PokeType.FIRE, 0.5);
    }

    double getEffectiveness(PokeType atk, PokeType def) {
        return eff.getOrDefault(atk, Collections.emptyMap()).getOrDefault(def, 1.0);
    }

    void initPokedex() {
        pokedex.add(new Pokemon("Charmander", PokeType.FIRE, 39, 52, movesOf(PokeType.FIRE)));
        pokedex.add(new Pokemon("Vulpix", PokeType.FIRE, 38, 41, movesOf(PokeType.FIRE)));
        pokedex.add(new Pokemon("Growlithe", PokeType.FIRE, 55, 70, movesOf(PokeType.FIRE)));

        pokedex.add(new Pokemon("Squirtle", PokeType.WATER, 44, 48, movesOf(PokeType.WATER)));
        pokedex.add(new Pokemon("Psyduck", PokeType.WATER, 50, 52, movesOf(PokeType.WATER)));
        pokedex.add(new Pokemon("Poliwag", PokeType.WATER, 40, 50, movesOf(PokeType.WATER)));

        pokedex.add(new Pokemon("Bulbasaur", PokeType.GRASS, 45, 49, movesOf(PokeType.GRASS)));
        pokedex.add(new Pokemon("Oddish", PokeType.GRASS, 45, 50, movesOf(PokeType.GRASS)));
        pokedex.add(new Pokemon("Bellsprout", PokeType.GRASS, 50, 75, movesOf(PokeType.GRASS)));

        pokedex.add(new Pokemon("Pidgey", PokeType.NORMAL, 40, 45, neutralMoves()));
        pokedex.add(new Pokemon("Rattata", PokeType.NORMAL, 30, 56, neutralMoves()));
        pokedex.add(new Pokemon("Sentret", PokeType.NORMAL, 35, 46, neutralMoves()));

        pokedex.add(new Pokemon("Geodude", PokeType.NORMAL, 40, 80, neutralMoves()));
        pokedex.add(new Pokemon("Zubat", PokeType.NORMAL, 40, 45, neutralMoves()));
        pokedex.add(new Pokemon("Meowth", PokeType.NORMAL, 40, 45, neutralMoves()));

        pokedex.add(new Pokemon("Odd-type1", PokeType.FIRE, 48, 60, movesOf(PokeType.FIRE)));
        pokedex.add(new Pokemon("Odd-type2", PokeType.WATER, 42, 56, movesOf(PokeType.WATER)));
        pokedex.add(new Pokemon("Odd-type3", PokeType.GRASS, 46, 54, movesOf(PokeType.GRASS)));
        pokedex.add(new Pokemon("Eevee", PokeType.NORMAL, 55, 55, neutralMoves()));
        pokedex.add(new Pokemon("Pikachu", PokeType.NORMAL, 35, 55, neutralMoves()));
    }

    java.util.List<Move> movesOf(PokeType t) {
        java.util.List<Move> m = new ArrayList<>();
        m.add(new Move(t.name() + " Strike", t, 50));
        m.add(new Move("Tackle", PokeType.NORMAL, 40));
        m.add(new Move("Quick Hit", PokeType.NORMAL, 30));
        return m;
    }

    java.util.List<Move> neutralMoves() {
        java.util.List<Move> m = new ArrayList<>();
        m.add(new Move("Slash", PokeType.NORMAL, 50));
        m.add(new Move("Tackle", PokeType.NORMAL, 40));
        m.add(new Move("Bite", PokeType.NORMAL, 35));
        return m;
    }

    Pokemon copyPokemon(Pokemon p) {
        java.util.List<Move> mv = new ArrayList<>();
        for (Move mm : p.moves) mv.add(new Move(mm.name, mm.type, mm.power));
        Pokemon x = new Pokemon(p.name, p.type, p.maxHp, p.attack, mv);
        x.sprite = p.sprite; // refer to sprite (shared)
        x.heal();
        return x;
    }

    // ----------------- Sprite loading -----------------

    void loadSprites() {
        // Attempt to load images from ./images/<name>.png (lowercase, spaces->_)
        for (Pokemon p : pokedex) {
            String fname = "images/" + p.name.toLowerCase().replace(' ', '_') + ".png";
            File f = new File(fname);
            if (f.exists()) {
                try {
                    ImageIcon icon = new ImageIcon(fname);
                    p.sprite = icon;
                } catch (Exception ex) {
                    p.sprite = placeholderIcon(p.name, SPRITE_SIZE.width, SPRITE_SIZE.height);
                }
            } else {
                p.sprite = placeholderIcon(p.name, SPRITE_SIZE.width, SPRITE_SIZE.height);
            }
        }
    }

    ImageIcon placeholderIcon(String text, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(200,220,255));
        g.fillRect(0,0,w,h);
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        drawCenteredString(g, text, new Rectangle(0,0,w,h), g.getFont());
        g.dispose();
        return new ImageIcon(img);
    }

    void drawCenteredString(Graphics2D g, String text, Rectangle rect, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }

    ImageIcon scaledIcon(ImageIcon in, int w, int h) {
        if (in == null) return placeholderIcon("No Image", w, h);
        Image img = in.getImage();
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
