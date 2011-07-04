package org.sonar.c.checks;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.sonar.c.api.CKeyword;
import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.DoNotUseContinue", name = "Continue statement must not be used", isoCategory = IsoCategory.Usability, priority = Priority.MAJOR,
	    description = "<p>Avoid using keyword continue.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)

public class ContinueCheck extends CCheck
{
    @Override
    public void init() {
        subscribeTo(getCGrammar().jumpStatement);
    }

    public void visitNode(AstNode node) {
        if (node.hasChildren(CKeyword.CONTINUE)) {
            log("Avoid using keyword continue.", node);
        }
    }
}
