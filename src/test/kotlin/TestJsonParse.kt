
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.lake.json2dart.config.ProjectConfig
import com.lake.json2dart.extension.Log.e
import com.lake.json2dart.extension.MessageTip.show
import com.lake.json2dart.generator.DartClassMaker
import com.lake.json2dart.view.JsonTableFrame
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test


class TestJsonParse {

    private val rawJson = """
{
  "id": 5753709,
  "folder_id": 0,
  "type": "image",
  "name": "test.png",
  "full_size_url": "XXX",
  "thumbnail_url": "XXX",
  "size": 47199,
  "created_at": "2021-07-25T16:05:33+00:00",
  "created_by": "Édouard Marquez",
  "width": 1180,
  "height": 300,
  "_links": [
    {
      "rel": "self",
      "href": "https://us19.api.mailchimp.com/3.0/file-manager/files/5753709",
      "method": "GET",
      "targetSchema": "https://us19.api.mailchimp.com/schema/3.0/Definitions/FileManager/Files/Response.json"
    },
    {
      "rel": "update",
      "href": "https://us19.api.mailchimp.com/3.0/file-manager/files/5753709",
      "method": "PATCH",
      "targetSchema": "https://us19.api.mailchimp.com/schema/3.0/Definitions/FileManager/Files/Response.json",
      "schema": "https://us19.api.mailchimp.com/schema/3.0/Definitions/FileManager/Files/PATCH.json"
    },
    {
      "rel": "delete",
      "href": "https://us19.api.mailchimp.com/3.0/file-manager/files/5753709",
      "method": "DELETE",
      "targetSchema": "https://us19.api.mailchimp.com/schema/3.0/FileManager/Files/Instance.json"
    },
    {
      "rel": "parent",
      "href": "https://us19.api.mailchimp.com/3.0/file-manager/files",
      "method": "GET",
      "targetSchema": "https://us19.api.mailchimp.com/schema/3.0/Definitions/FileManager/Files/CollectionResponse.json",
      "schema": "https://us19.api.mailchimp.com/schema/3.0/Paths/FileManager/Files/Collection.json"
    }
  ]
}
    """.trimIndent()

    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp() {
            ProjectConfig.setupTestInitState()
        }
    }

    @Test
    fun test() {
        try {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonParser = JsonParser()
            val jsonElement: JsonElement = jsonParser.parse(rawJson)
            val jsonStr = gson.toJson(jsonElement)
            println(jsonStr)
            val dartClass = DartClassMaker("Test", jsonStr).makeDartClass()
            val dialog = JsonTableFrame.Builder()
                .setDartClass(dartClass)
                .setFileName("test")
                .build()
            dialog.setSize(800, 600)
            dialog.setLocationRelativeTo(null)
            dialog.isVisible = true
        } catch (e: JsonSyntaxException) {
            show("Json Syntax Error")
            return
        } catch (e: Exception) {
            e(e)
        }
    }
}