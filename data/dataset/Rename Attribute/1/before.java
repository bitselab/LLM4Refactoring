package com.puppycrawl.tools.checkstyle.checks.imports;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.FileStatefulCheck;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

@FileStatefulCheck
public class CustomImportOrderCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_LINE_SEPARATOR = "custom.import.order.line.separator";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_SEPARATED_IN_GROUP = "custom.import.order.separated.internally";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_LEX = "custom.import.order.lex";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_NONGROUP_IMPORT = "custom.import.order.nonGroup.import";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_NONGROUP_EXPECTED = "custom.import.order.nonGroup.expected";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_ORDER = "custom.import.order";

    /** STATIC group name. */
    public static final String STATIC_RULE_GROUP = "STATIC";

    /** SAME_PACKAGE group name. */
    public static final String SAME_PACKAGE_RULE_GROUP = "SAME_PACKAGE";

    /** THIRD_PARTY_PACKAGE group name. */
    public static final String THIRD_PARTY_PACKAGE_RULE_GROUP = "THIRD_PARTY_PACKAGE";

    /** STANDARD_JAVA_PACKAGE group name. */
    public static final String STANDARD_JAVA_PACKAGE_RULE_GROUP = "STANDARD_JAVA_PACKAGE";

    /** SPECIAL_IMPORTS group name. */
    public static final String SPECIAL_IMPORTS_RULE_GROUP = "SPECIAL_IMPORTS";

    /** NON_GROUP group name. */
    private static final String NON_GROUP_RULE_GROUP = "NOT_ASSIGNED_TO_ANY_GROUP";

    /** Pattern used to separate groups of imports. */
    private static final Pattern GROUP_SEPARATOR_PATTERN = Pattern.compile("\\s*###\\s*");

    /** Specify format of order declaration customizing by user. */
    private static final String customImportOrderRules = "";

    /** Processed list of import order rules. */
    private final List<String> customOrderRules = new ArrayList<>();

    /** Contains objects with import attributes. */
    private final List<ImportDetails> importToGroupList = new ArrayList<>();

    /** Specify RegExp for SAME_PACKAGE group imports. */
    private String samePackageDomainsRegExp = "";

    /** Specify RegExp for STANDARD_JAVA_PACKAGE group imports. */
    private Pattern standardPackageRegExp = Pattern.compile("^(java|javax)\\.");

    /** Specify RegExp for THIRD_PARTY_PACKAGE group imports. */
    private Pattern thirdPartyPackageRegExp = Pattern.compile(".*");

    /** Specify RegExp for SPECIAL_IMPORTS group imports. */
    private Pattern specialImportsRegExp = Pattern.compile("^$");

    /** Force empty line separator between import groups. */
    private boolean separateLineBetweenGroups = true;

    /**
     * Force grouping alphabetically,
     * in <a href="https://en.wikipedia.org/wiki/ASCII#Order"> ASCII sort order</a>.
     */
    private boolean sortImportsInGroupAlphabetically;

    /** Number of first domains for SAME_PACKAGE group. */
    private int samePackageMatchingDepth = 2;

    /**
     * Setter to specify RegExp for STANDARD_JAVA_PACKAGE group imports.
     *
     * @param regexp
     *        user value.
     */
    public final void setStandardPackageRegExp(Pattern regexp) {
        standardPackageRegExp = regexp;
    }

    /**
     * Setter to specify RegExp for THIRD_PARTY_PACKAGE group imports.
     *
     * @param regexp
     *        user value.
     */
    public final void setThirdPartyPackageRegExp(Pattern regexp) {
        thirdPartyPackageRegExp = regexp;
    }

    /**
     * Setter to specify RegExp for SPECIAL_IMPORTS group imports.
     *
     * @param regexp
     *        user value.
     */
    public final void setSpecialImportsRegExp(Pattern regexp) {
        specialImportsRegExp = regexp;
    }

    /**
     * Setter to force empty line separator between import groups.
     *
     * @param value
     *        user value.
     */
    public final void setSeparateLineBetweenGroups(boolean value) {
        separateLineBetweenGroups = value;
    }

    /**
     * Setter to force grouping alphabetically, in
     * <a href="https://en.wikipedia.org/wiki/ASCII#Order">ASCII sort order</a>.
     *
     * @param value
     *        user value.
     */
    public final void setSortImportsInGroupAlphabetically(boolean value) {
        sortImportsInGroupAlphabetically = value;
    }

    /**
     * Setter to specify format of order declaration customizing by user.
     *
     * @param inputCustomImportOrder
     *        user value.
     */
    public final void setCustomImportOrderRules(final String inputCustomImportOrder) {
        if (!customImportOrderRules.equals(inputCustomImportOrder)) {
            for (String currentState : GROUP_SEPARATOR_PATTERN.split(inputCustomImportOrder)) {
                addRulesToList(currentState);
            }
            customOrderRules.add(NON_GROUP_RULE_GROUP);
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[] {
                TokenTypes.IMPORT,
                TokenTypes.STATIC_IMPORT,
                TokenTypes.PACKAGE_DEF,
        };
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        importToGroupList.clear();
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.PACKAGE_DEF) {
            samePackageDomainsRegExp = createSamePackageRegexp(
                    samePackageMatchingDepth, ast);
        }
        else {
            final String importFullPath = getFullImportIdent(ast);
            final boolean isStatic = ast.getType() == TokenTypes.STATIC_IMPORT;
            importToGroupList.add(new ImportDetails(importFullPath,
                    getImportGroup(isStatic, importFullPath), isStatic, ast));
        }
    }

    @Override
    public void finishTree(DetailAST rootAST) {
        if (!importToGroupList.isEmpty()) {
            finishImportList();
        }
    }

    /** Examine the order of all the imports and log any violations. */
    private void finishImportList() {
        String currentGroup = getFirstGroup();
        int currentGroupNumber = customOrderRules.lastIndexOf(currentGroup);
        ImportDetails previousImportObjectFromCurrentGroup = null;
        String previousImportFromCurrentGroup = null;

        for (ImportDetails importObject : importToGroupList) {
            final String importGroup = importObject.getImportGroup();
            final String fullImportIdent = importObject.getImportFullPath();

            if (importGroup.equals(currentGroup)) {
                validateExtraEmptyLine(previousImportObjectFromCurrentGroup,
                        importObject, fullImportIdent);
                if (isAlphabeticalOrderBroken(previousImportFromCurrentGroup, fullImportIdent)) {
                    log(importObject.getImportAST(), MSG_LEX,
                            fullImportIdent, previousImportFromCurrentGroup);
                }
                else {
                    previousImportFromCurrentGroup = fullImportIdent;
                }
                previousImportObjectFromCurrentGroup = importObject;
            }
            else {
                // not the last group, last one is always NON_GROUP
                if (customOrderRules.size() > currentGroupNumber + 1) {
                    final String nextGroup = getNextImportGroup(currentGroupNumber + 1);
                    if (importGroup.equals(nextGroup)) {
                        validateMissedEmptyLine(previousImportObjectFromCurrentGroup,
                                importObject, fullImportIdent);
                        currentGroup = nextGroup;
                        currentGroupNumber = customOrderRules.lastIndexOf(nextGroup);
                        previousImportFromCurrentGroup = fullImportIdent;
                    }
                    else {
                        logWrongImportGroupOrder(importObject.getImportAST(),
                                importGroup, nextGroup, fullImportIdent);
                    }
                    previousImportObjectFromCurrentGroup = importObject;
                }
                else {
                    logWrongImportGroupOrder(importObject.getImportAST(),
                            importGroup, currentGroup, fullImportIdent);
                }
            }
        }
    }

    /**
     * Log violation if empty line is missed.
     *
     * @param previousImport previous import from current group.
     * @param importObject current import.
     * @param fullImportIdent full import identifier.
     */
    private void validateMissedEmptyLine(ImportDetails previousImport,
                                         ImportDetails importObject, String fullImportIdent) {
        if (isEmptyLineMissed(previousImport, importObject)) {
            log(importObject.getImportAST(), MSG_LINE_SEPARATOR, fullImportIdent);
        }
    }

    /**
     * Log violation if extra empty line is present.
     *
     * @param previousImport previous import from current group.
     * @param importObject current import.
     * @param fullImportIdent full import identifier.
     */
    private void validateExtraEmptyLine(ImportDetails previousImport,
                                        ImportDetails importObject, String fullImportIdent) {
        if (isSeparatedByExtraEmptyLine(previousImport, importObject)) {
            log(importObject.getImportAST(), MSG_SEPARATED_IN_GROUP, fullImportIdent);
        }
    }

    /**
     * Get first import group.
     *
     * @return
     *        first import group of file.
     */
    private String getFirstGroup() {
        final ImportDetails firstImport = importToGroupList.get(0);
        return getImportGroup(firstImport.isStaticImport(),
                firstImport.getImportFullPath());
    }

    /**
     * Examine alphabetical order of imports.
     *
     * @param previousImport
     *        previous import of current group.
     * @param currentImport
     *        current import.
     * @return
     *        true, if previous and current import are not in alphabetical order.
     */
    private boolean isAlphabeticalOrderBroken(String previousImport,
                                              String currentImport) {
        return sortImportsInGroupAlphabetically
                && previousImport != null
                && compareImports(currentImport, previousImport) < 0;
    }

    /**
     * Examine empty lines between groups.
     *
     * @param previousImportObject
     *        previous import in current group.
     * @param currentImportObject
     *        current import.
     * @return
     *        true, if current import NOT separated from previous import by empty line.
     */
    private boolean isEmptyLineMissed(ImportDetails previousImportObject,
                                      ImportDetails currentImportObject) {
        return separateLineBetweenGroups
                && getCountOfEmptyLinesBetween(
                previousImportObject.getEndLineNumber(),
                currentImportObject.getStartLineNumber()) != 1;
    }

    /**
     * Examine that imports separated by more than one empty line.
     *
     * @param previousImportObject
     *        previous import in current group.
     * @param currentImportObject
     *        current import.
     * @return
     *        true, if current import separated from previous by more than one empty line.
     */
    private boolean isSeparatedByExtraEmptyLine(ImportDetails previousImportObject,
                                                ImportDetails currentImportObject) {
        return previousImportObject != null
                && getCountOfEmptyLinesBetween(
                previousImportObject.getEndLineNumber(),
                currentImportObject.getStartLineNumber()) > 0;
    }

    /**
     * Log wrong import group order.
     *
     * @param importAST
     *        import ast.
     * @param importGroup
     *        import group.
     * @param currentGroupNumber
     *        current group number we are checking.
     * @param fullImportIdent
     *        full import name.
     */
    private void logWrongImportGroupOrder(DetailAST importAST, String importGroup,
                                          String currentGroupNumber, String fullImportIdent) {
        if (NON_GROUP_RULE_GROUP.equals(importGroup)) {
            log(importAST, MSG_NONGROUP_IMPORT, fullImportIdent);
        }
        else if (NON_GROUP_RULE_GROUP.equals(currentGroupNumber)) {
            log(importAST, MSG_NONGROUP_EXPECTED, importGroup, fullImportIdent);
        }
        else {
            log(importAST, MSG_ORDER, importGroup, currentGroupNumber, fullImportIdent);
        }
    }

    /**
     * Get next import group.
     *
     * @param currentGroupNumber
     *        current group number.
     * @return
     *        next import group.
     */
    private String getNextImportGroup(int currentGroupNumber) {
        int nextGroupNumber = currentGroupNumber;

        while (customOrderRules.size() > nextGroupNumber + 1) {
            if (hasAnyImportInCurrentGroup(customOrderRules.get(nextGroupNumber))) {
                break;
            }
            nextGroupNumber++;
        }
        return customOrderRules.get(nextGroupNumber);
    }

    /**
     * Checks if current group contains any import.
     *
     * @param currentGroup
     *        current group.
     * @return
     *        true, if current group contains at least one import.
     */
    private boolean hasAnyImportInCurrentGroup(String currentGroup) {
        boolean result = false;
        for (ImportDetails currentImport : importToGroupList) {
            if (currentGroup.equals(currentImport.getImportGroup())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Get import valid group.
     *
     * @param isStatic
     *        is static import.
     * @param importPath
     *        full import path.
     * @return import valid group.
     */
    private String getImportGroup(boolean isStatic, String importPath) {
        RuleMatchForImport bestMatch = new RuleMatchForImport(NON_GROUP_RULE_GROUP, 0, 0);
        if (isStatic && customOrderRules.contains(STATIC_RULE_GROUP)) {
            bestMatch.group = STATIC_RULE_GROUP;
            bestMatch.matchLength = importPath.length();
        }
        else if (customOrderRules.contains(SAME_PACKAGE_RULE_GROUP)) {
            final String importPathTrimmedToSamePackageDepth =
                    getFirstDomainsFromIdent(samePackageMatchingDepth, importPath);
            if (samePackageDomainsRegExp.equals(importPathTrimmedToSamePackageDepth)) {
                bestMatch.group = SAME_PACKAGE_RULE_GROUP;
                bestMatch.matchLength = importPath.length();
            }
        }
        for (String group : customOrderRules) {
            if (STANDARD_JAVA_PACKAGE_RULE_GROUP.equals(group)) {
                bestMatch = findBetterPatternMatch(importPath,
                        STANDARD_JAVA_PACKAGE_RULE_GROUP, standardPackageRegExp, bestMatch);
            }
            if (SPECIAL_IMPORTS_RULE_GROUP.equals(group)) {
                bestMatch = findBetterPatternMatch(importPath,
                        group, specialImportsRegExp, bestMatch);
            }
        }

        if (NON_GROUP_RULE_GROUP.equals(bestMatch.group)
                && customOrderRules.contains(THIRD_PARTY_PACKAGE_RULE_GROUP)
                && thirdPartyPackageRegExp.matcher(importPath).find()) {
            bestMatch.group = THIRD_PARTY_PACKAGE_RULE_GROUP;
        }
        return bestMatch.group;
    }

    /**
     * Tries to find better matching regular expression:
     * longer matching substring wins; in case of the same length,
     * lower position of matching substring wins.
     *
     * @param importPath
     *      Full import identifier
     * @param group
     *      Import group we are trying to assign the import
     * @param regExp
     *      Regular expression for import group
     * @param currentBestMatch
     *      object with currently best match
     * @return better match (if found) or the same (currentBestMatch)
     */
    private static RuleMatchForImport findBetterPatternMatch(String importPath, String group,
                                                             Pattern regExp, RuleMatchForImport currentBestMatch) {
        RuleMatchForImport betterMatchCandidate = currentBestMatch;
        final Matcher matcher = regExp.matcher(importPath);
        while (matcher.find()) {
            final int matchStart = matcher.start();
            final int length = matcher.end() - matchStart;
            if (length > betterMatchCandidate.matchLength
                    || length == betterMatchCandidate.matchLength
                    && matchStart < betterMatchCandidate.matchPosition) {
                betterMatchCandidate = new RuleMatchForImport(group, length, matchStart);
            }
        }
        return betterMatchCandidate;
    }

    /**
     * Checks compare two import paths.
     *
     * @param import1
     *        current import.
     * @param import2
     *        previous import.
     * @return a negative integer, zero, or a positive integer as the
     *        specified String is greater than, equal to, or less
     *        than this String, ignoring case considerations.
     */
    private static int compareImports(String import1, String import2) {
        int result = 0;
        final String separator = "\\.";
        final String[] import1Tokens = import1.split(separator);
        final String[] import2Tokens = import2.split(separator);
        for (int i = 0; i != import1Tokens.length && i != import2Tokens.length; i++) {
            final String import1Token = import1Tokens[i];
            final String import2Token = import2Tokens[i];
            result = import1Token.compareTo(import2Token);
            if (result != 0) {
                break;
            }
        }
        if (result == 0) {
            result = Integer.compare(import1Tokens.length, import2Tokens.length);
        }
        return result;
    }

    /**
     * Counts empty lines between given parameters.
     *
     * @param fromLineNo
     *        One-based line number of previous import.
     * @param toLineNo
     *        One-based line number of current import.
     * @return count of empty lines between given parameters, exclusive,
     *        eg., (fromLineNo, toLineNo).
     */
    private int getCountOfEmptyLinesBetween(int fromLineNo, int toLineNo) {
        int result = 0;
        final String[] lines = getLines();

        for (int i = fromLineNo + 1; i <= toLineNo - 1; i++) {
            // "- 1" because the numbering is one-based
            if (CommonUtil.isBlank(lines[i - 1])) {
                result++;
            }
        }
        return result;
    }

    /**
     * Forms import full path.
     *
     * @param token
     *        current token.
     * @return full path or null.
     */
    private static String getFullImportIdent(DetailAST token) {
        String ident = "";
        if (token != null) {
            ident = FullIdent.createFullIdent(token.findFirstToken(TokenTypes.DOT)).getText();
        }
        return ident;
    }

    /**
     * Parses ordering rule and adds it to the list with rules.
     *
     * @param ruleStr
     *        String with rule.
     * @throws IllegalArgumentException when SAME_PACKAGE rule parameter is not positive integer
     * @throws IllegalStateException when ruleStr is unexpected value
     */
    private void addRulesToList(String ruleStr) {
        if (STATIC_RULE_GROUP.equals(ruleStr)
                || THIRD_PARTY_PACKAGE_RULE_GROUP.equals(ruleStr)
                || STANDARD_JAVA_PACKAGE_RULE_GROUP.equals(ruleStr)
                || SPECIAL_IMPORTS_RULE_GROUP.equals(ruleStr)) {
            customOrderRules.add(ruleStr);
        }
        else if (ruleStr.startsWith(SAME_PACKAGE_RULE_GROUP)) {
            final String rule = ruleStr.substring(ruleStr.indexOf('(') + 1,
                    ruleStr.indexOf(')'));
            samePackageMatchingDepth = Integer.parseInt(rule);
            if (samePackageMatchingDepth <= 0) {
                throw new IllegalArgumentException(
                        "SAME_PACKAGE rule parameter should be positive integer: " + ruleStr);
            }
            customOrderRules.add(SAME_PACKAGE_RULE_GROUP);
        }
        else {
            throw new IllegalStateException("Unexpected rule: " + ruleStr);
        }
    }

    /**
     * Creates samePackageDomainsRegExp of the first package domains.
     *
     * @param firstPackageDomainsCount
     *        number of first package domains.
     * @param packageNode
     *        package node.
     * @return same package regexp.
     */
    private static String createSamePackageRegexp(int firstPackageDomainsCount,
                                                  DetailAST packageNode) {
        final String packageFullPath = getFullImportIdent(packageNode);
        return getFirstDomainsFromIdent(firstPackageDomainsCount, packageFullPath);
    }

    /**
     * Extracts defined amount of domains from the left side of package/import identifier.
     *
     * @param firstPackageDomainsCount
     *        number of first package domains.
     * @param packageFullPath
     *        full identifier containing path to package or imported object.
     * @return String with defined amount of domains or full identifier
     *        (if full identifier had less domain than specified)
     */
    private static String getFirstDomainsFromIdent(
            final int firstPackageDomainsCount, final String packageFullPath) {
        final StringBuilder builder = new StringBuilder(256);
        final StringTokenizer tokens = new StringTokenizer(packageFullPath, ".");
        int count = firstPackageDomainsCount;

        while (count > 0 && tokens.hasMoreTokens()) {
            builder.append(tokens.nextToken()).append('.');
            count--;
        }
        return builder.toString();
    }

    /**
     * Contains import attributes as line number, import full path, import
     * group.
     */
    private static final class ImportDetails {

        /** Import full path. */
        private final String importFullPath;

        /** Import group. */
        private final String importGroup;

        /** Is static import. */
        private final boolean staticImport;

        /** Import AST. */
        private final DetailAST importAST;

        /**
         * Initialise importFullPath, importGroup, staticImport, importAST.
         *
         * @param importFullPath
         *        import full path.
         * @param importGroup
         *        import group.
         * @param staticImport
         *        if import is static.
         * @param importAST
         *        import ast
         */
        private ImportDetails(String importFullPath, String importGroup, boolean staticImport,
                              DetailAST importAST) {
            this.importFullPath = importFullPath;
            this.importGroup = importGroup;
            this.staticImport = staticImport;
            this.importAST = importAST;
        }

        /**
         * Get import full path variable.
         *
         * @return import full path variable.
         */
        public String getImportFullPath() {
            return importFullPath;
        }

        /**
         * Get import start line number from ast.
         *
         * @return import start line from ast.
         */
        public int getStartLineNumber() {
            return importAST.getLineNo();
        }

        /**
         * Get import end line number from ast.
         * <p>
         * <b>Note:</b> It can be different from <b>startLineNumber</b> when import statement span
         * multiple lines.
         * </p>
         *
         * @return import end line from ast.
         */
        public int getEndLineNumber() {
            return importAST.getLastChild().getLineNo();
        }

        /**
         * Get import group.
         *
         * @return import group.
         */
        public String getImportGroup() {
            return importGroup;
        }

        /**
         * Checks if import is static.
         *
         * @return true, if import is static.
         */
        public boolean isStaticImport() {
            return staticImport;
        }

        /**
         * Get import ast.
         *
         * @return import ast.
         */
        public DetailAST getImportAST() {
            return importAST;
        }

    }

    /**
     * Contains matching attributes assisting in definition of "best matching"
     * group for import.
     */
    private static final class RuleMatchForImport {

        /** Position of matching string for current best match. */
        private final int matchPosition;
        /** Length of matching string for current best match. */
        private int matchLength;
        /** Import group for current best match. */
        private String group;

        /**
         * Constructor to initialize the fields.
         *
         * @param group
         *        Matched group.
         * @param length
         *        Matching length.
         * @param position
         *        Matching position.
         */
        private RuleMatchForImport(String group, int length, int position) {
            this.group = group;
            matchLength = length;
            matchPosition = position;
        }

    }

}