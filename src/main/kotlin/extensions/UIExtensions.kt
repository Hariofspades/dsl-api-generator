package extensions

import java.awt.Dialog
import java.awt.Toolkit

fun Dialog.showDialog(width: Int = 550, height: Int = 400, isInCenter: Boolean = true, isResizable: Boolean = true) {
    pack()
    this.isResizable = isResizable
    setSize(width, height)
    if (isInCenter) {
        setLocation(
                Toolkit.getDefaultToolkit().screenSize.width / 2 - width / 2,
                Toolkit.getDefaultToolkit().screenSize.height / 2 - height / 2
        )
    }
    isVisible = true
}