package util

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiDocumentManager

object CodeUtil {

    fun insert(generatedCode: String, editor: Editor) {
        val document = editor.document
        WriteCommandAction.runWriteCommandAction(editor.project) {
            document.replaceString(editor.selectionModel.selectionStart, editor.selectionModel.selectionEnd, generatedCode)
            editor.project?.let { project ->
                PsiDocumentManager.getInstance(project).commitDocument(document)
            }
        }
        editor.selectionModel.removeSelection()
    }
}