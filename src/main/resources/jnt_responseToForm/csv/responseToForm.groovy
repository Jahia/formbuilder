import java.text.SimpleDateFormat
import org.jahia.services.content.JCRContentUtils

print new SimpleDateFormat("yyyy-MM-dd HH:mm").format(currentNode.properties['jcr:created'].date.time)
print ","
print currentNode.properties['jcr:createdBy'].string
print ","
print currentNode.properties['originUrl'].string
formFields.each() { k, v ->
    if (v != "file") {
        print ","
        p = currentNode.properties[k]
        if (p != null) print p.string
    }
}
if (currentResource.moduleParams.hasFile) {
    print ","
    files = JCRContentUtils.getChildrenOfType(currentNode, "jnt:file")
    files.eachWithIndex() { file, index ->
        if (index != 0) {
            print " "
        }
        print file.url
    }
}