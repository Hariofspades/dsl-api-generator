package util

import com.intellij.openapi.util.io.FileUtil
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.log.NullLogChute
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import java.io.StringWriter
import java.util.*

object VelocityEngineUtil {

    private lateinit var engine: VelocityEngine

    fun initialise() {
        val properties = Properties().apply {
            setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
            setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogChute::class.java.name)
            setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name)
        }
        engine = VelocityEngine(properties).apply { init() }
    }

    fun evaluate(map: LinkedHashMap<String, String>, className: String, pascalCase: Boolean): String {
        val velocityTemplate: String = FileUtil.loadTextAndClose(this::class.java.getResourceAsStream("/template/DSLBuilder.vm"))
        println("VelocityEngineHelper: velocityTemplate is $velocityTemplate")
        val velocityContext = VelocityContext().apply {
            put("actionParamMap", map)
            put("className", className)
            put("pascalCase", pascalCase)
        }
        val sWriter = StringWriter()
        engine.evaluate(velocityContext, sWriter, "", velocityTemplate)
        return sWriter.toString()
    }
}