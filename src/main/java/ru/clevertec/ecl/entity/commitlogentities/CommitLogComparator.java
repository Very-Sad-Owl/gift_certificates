package ru.clevertec.ecl.entity.commitlogentities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.clevertec.ecl.exception.UndefinedException;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.clevertec.ecl.service.common.DatabaseConstants.*;

/**
 * Comparator class for sorting {@link ru.clevertec.ecl.entity.commitlogentities.CommitLog} objects.
 *
 * See also {@link java.util.Comparator}.
 *
 * @author Olga Mailychko
 *
 */
public class CommitLogComparator implements Comparator<CommitLog> {
    private static final String ID_PATTERN_TEMPLATE = "\\{\"id\":(?<id>\\d).*}";

    @Override
    public int compare(CommitLog o1, CommitLog o2) {
        long o1Id = o1.getEntityId();
        long o2Id = o2.getEntityId();
        TablePriority o1Table = TablePriority.findByTitle(o1.getTableTitle());
        TablePriority o2Table = TablePriority.findByTitle(o2.getTableTitle());
        if (o1.getAction() == o2.getAction()) {
            if (o1Table.equals(o2Table)) {
                return Long.compare(o1Id, o2Id);
            } else {
                return Integer.compare(o1Table.ordinal(), o2Table.ordinal());
            }
        } else if (o1.getAction() == Action.SAVE) {
            return -1;
        } else if (o2.getAction() == Action.SAVE) {
            return 1;
        } else if (o1.getAction() == Action.UPDATE) {
            return -1;
        } else if (o2.getAction() == Action.UPDATE) {
            return 1;
        } else if (o1.getAction() == Action.DELETE) {
            return 1;
        } else if (o2.getAction() == Action.DELETE) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Enum containing observed by commit log tables' titles
     *
     * Provided enums are used for comparator logic. Corresponding tables are compared by using
     * constant's ordinal number. The bigger ordinal number, the lesser priority of ist table.
     *
     * See also {@link javax.persistence.Enumerated}.
     *
     * @author Olga Mailychko
     *
     */
    @AllArgsConstructor
    @Getter
    enum TablePriority {
        TAG(ALIAS_TAGS), CERTIFICATE(ALIAS_CERTIFICATES), ORDER(ALIAS_ORDERS);

        private String title;
        private static final Map<String, TablePriority> map;

        static {
            map = new HashMap<>();
            for (TablePriority v : TablePriority.values()) {
                map.put(v.title, v);
            }
        }

        public static TablePriority findByTitle(String title) {
            return map.get(title);
        }
    }

}
