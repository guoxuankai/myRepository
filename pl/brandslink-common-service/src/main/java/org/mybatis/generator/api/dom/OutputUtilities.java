//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.mybatis.generator.api.dom;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

public class OutputUtilities {
    private static final String lineSeparator;

    private OutputUtilities() {
    }

    public static void javaIndent(StringBuilder sb, int indentLevel) {
        for(int i = 0; i < indentLevel; ++i) {
            sb.append("    ");
        }

    }

    public static void xmlIndent(StringBuilder sb, int indentLevel) {
        for(int i = 0; i < indentLevel; ++i) {
            sb.append("    ");
        }

    }

    public static void newLine(StringBuilder sb) {
        sb.append(lineSeparator);
    }

    public static Set<String> calculateImports(Set<FullyQualifiedJavaType> importedTypes) {
        StringBuilder sb = new StringBuilder();
        Set<String> importStrings = new TreeSet();
        Iterator var3 = importedTypes.iterator();

        while(var3.hasNext()) {
            FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType)var3.next();
            Iterator var5 = fqjt.getImportList().iterator();

            while(var5.hasNext()) {
                String importString = (String)var5.next();
                sb.setLength(0);
                sb.append("import ");
                sb.append(importString);
                sb.append(';');
                importStrings.add(sb.toString());
            }
        }

        return importStrings;
    }

    static {
        String ls = System.getProperty("line.separator");
        if (ls == null) {
            ls = "\n";
        }

        lineSeparator = ls;
    }
}
