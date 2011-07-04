package org.sonar.c.checks;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.sonar.c.api.CKeyword;
import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.DoNotUseBreak", name = "Break statement must be used only inside a switch block", isoCategory = IsoCategory.Usability, priority = Priority.MAJOR,
	    description = "<p>Avoid using keyword break outside a switch block.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)

public class BreakCheck extends CCheck
{
    @Override
    public void init() {
        subscribeTo(getCGrammar().jumpStatement);
    }

    public void visitNode(AstNode node)
    {
        if((!node.hasParents(getCGrammar().switchStatement)) && 
            (node.hasChildren(CKeyword.BREAK))) {
            log("Keyword break only allowed inside a switch block.", node);
        }
    }
}
