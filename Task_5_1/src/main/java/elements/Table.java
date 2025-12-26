package elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Table extends Element {
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;

    private final List<String[]> rows;
    private final int[] alignments;
    private final int rowLimit;

    private Table(Builder builder) {
        this.rows = new ArrayList<>(builder.rows);
        this.alignments = builder.alignments;
        this.rowLimit = builder.rowLimit;
    }

    @Override
    public String serialize() {
        if (rows.isEmpty()) return "";

        int cols = rows.get(0).length;
        int[] colWidths = new int[cols];

        int actualRows = Math.min(rows.size(), rowLimit);
        for (int r = 0; r < actualRows; r++) {
            for (int c = 0; c < cols; c++) {
                if (c < rows.get(r).length) {
                    colWidths[c] = Math.max(colWidths[c], rows.get(r)[c].length());
                }
            }
        }
        for (int c = 0; c < cols; c++) {
            colWidths[c] = Math.max(colWidths[c], 3);
        }

        StringBuilder sb = new StringBuilder();

        sb.append("|");
        for (int c = 0; c < cols; c++) {
            sb.append(" ").append(pad(rows.get(0)[c], colWidths[c])).append(" |");
        }
        sb.append("\n");

        sb.append("|");
        for (int c = 0; c < cols; c++) {
            int align = (c < alignments.length) ? alignments[c] : ALIGN_LEFT;
            sb.append(" ");
            String dashes;
            switch (align) {
                case ALIGN_CENTER:
                    dashes = ":" + "-".repeat(colWidths[c] - 2) + ":";
                    break;
                case ALIGN_RIGHT:
                    dashes = "-".repeat(colWidths[c] - 1) + ":";
                    break;
                case ALIGN_LEFT:
                default:
                    dashes = "-".repeat(colWidths[c]);
                    break;
            }
            sb.append(dashes).append(" |");
        }
        sb.append("\n");

        for (int r = 1; r < actualRows; r++) {
            sb.append("|");
            for (int c = 0; c < cols; c++) {
                String val = (c < rows.get(r).length) ? rows.get(r)[c] : "";
                sb.append(" ").append(pad(val, colWidths[c])).append(" |");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String pad(String s, int width) {
        if (s == null) s = "";
        return s + " ".repeat(Math.max(0, width - s.length()));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Table table = (Table) obj;
        if (rowLimit != table.rowLimit) return false;
        if (!Arrays.equals(alignments, table.alignments)) return false;
        if (rows.size() != table.rows.size()) return false;
        for (int i = 0; i < rows.size(); i++) {
            if (!Arrays.equals(rows.get(i), table.rows.get(i))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (String[] row : rows) {
            result = 31 * result + Arrays.hashCode(row);
        }
        result = 31 * result + Arrays.hashCode(alignments);
        result = 31 * result + rowLimit;
        return result;
    }

    public static class Builder {
        private final List<String[]> rows = new ArrayList<>();
        private int[] alignments = new int[0];
        private int rowLimit = Integer.MAX_VALUE;

        public Builder withAlignments(int... aligns) {
            this.alignments = aligns;
            return this;
        }

        public Builder withRowLimit(int limit) {
            this.rowLimit = limit;
            return this;
        }

        public Builder addRow(Object... cells) {
            String[] row = new String[cells.length];
            for (int i = 0; i < cells.length; i++) {
                Object cell = cells[i];
                if (cell instanceof Element) {
                    row[i] = ((Element) cell).serialize();
                } else {
                    row[i] = String.valueOf(cell);
                }
            }
            rows.add(row);
            return this;
        }

        public Table build() {
            return new Table(this);
        }
    }
}
