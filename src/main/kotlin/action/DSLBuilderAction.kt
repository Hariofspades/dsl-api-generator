package action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR
import com.intellij.openapi.editor.Editor
import DslListenerBuilderDialog
import extensions.showDialog
import util.CodeUtil
import util.VelocityEngineUtil
import java.util.LinkedHashMap

class DSLBuilderAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        VelocityEngineUtil.initialise()
        DslListenerBuilderDialog().apply {
            setDialogListener(object: DslListenerBuilderDialog.DialogListener {
                override fun onCancelBtnClicked() {
                    // No implementation
                }

                override fun onGenerateClicked(
                    map: LinkedHashMap<String, String>?,
                    className: String,
                    pascalCase: Boolean
                ) {
                    if (!map.isNullOrEmpty()) {
                        generateCode(map, className, pascalCase, event)
                    } else print("map is empty")
                }
            })
            showDialog(height = 600)
        }
    }

    override fun update(event: AnActionEvent) {
        if (event.project == null) {
            event.presentation.isEnabled = false
        }
        val editor: Editor? = event.dataContext.getData(EDITOR)
        if (editor == null) {
            event.presentation.isEnabled = false
        }
        event.presentation.isEnabled = true
    }

    private fun generateCode(
            map: LinkedHashMap<String, String>,
            className: String,
            pascalCase: Boolean,
            event: AnActionEvent
    ) {
        val generateCode: String = VelocityEngineUtil.evaluate(map, className, pascalCase)
        val editor: Editor? = event.dataContext.getData(EDITOR)
        if (editor != null) {
            try {
                CodeUtil.insert(generateCode, editor)
                println("generated code is succeed, generated code is :\n $generateCode")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}