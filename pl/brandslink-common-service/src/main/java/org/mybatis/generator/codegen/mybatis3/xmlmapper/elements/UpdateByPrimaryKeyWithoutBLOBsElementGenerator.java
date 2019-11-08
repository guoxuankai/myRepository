//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import java.util.Iterator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

public class UpdateByPrimaryKeyWithoutBLOBsElementGenerator extends AbstractXmlElementGenerator {
    private boolean isSimple;

    public UpdateByPrimaryKeyWithoutBLOBsElementGenerator(boolean isSimple) {
        this.isSimple = isSimple;
    }

    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("update");
        answer.addAttribute(new Attribute("id", this.introspectedTable.getUpdateByPrimaryKeyStatementId()));
        answer.addAttribute(new Attribute("parameterType", this.introspectedTable.getBaseRecordType()));
        this.context.getCommentGenerator().addComment(answer);
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(this.introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        sb.append("set ");
        Iterator iter;
        if (this.isSimple) {
            iter = ListUtilities.removeGeneratedAlwaysColumns(this.introspectedTable.getNonPrimaryKeyColumns()).iterator();
        } else {
            iter = ListUtilities.removeGeneratedAlwaysColumns(this.introspectedTable.getBaseColumns()).iterator();
        }
        Boolean flag = false;
        while(iter.hasNext()) {
            IntrospectedColumn introspectedColumn = (IntrospectedColumn)iter.next();
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            if (introspectedColumn.getActualColumnName().equals("version")) {
                flag = true;
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn) + " + 1");
            } else {
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            }

            if (iter.hasNext()) {
                sb.append(',');
            }

            answer.addElement(new TextElement(sb.toString()));
            if (iter.hasNext()) {
                sb.setLength(0);
                OutputUtilities.xmlIndent(sb, 1);
            }
        }

        boolean and = false;
        Iterator var6 = this.introspectedTable.getPrimaryKeyColumns().iterator();

        while(var6.hasNext()) {
            IntrospectedColumn introspectedColumn = (IntrospectedColumn)var6.next();
            sb.setLength(0);
            if (and) {
                sb.append("  and ");
            } else {
                sb.append("where ");
                and = true;
            }

            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            answer.addElement(new TextElement(sb.toString()));
        }
        if (flag)
            answer.addElement(new TextElement("and version = #{version,jdbcType=BIGINT}"));
        if (this.context.getPlugins().sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(answer, this.introspectedTable)) {
            parentElement.addElement(answer);
        }

    }
}
