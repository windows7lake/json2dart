
import org.junit.Test

class TestString {

    fun String.toCamelCase(): String {
        val value = this
        return StringBuilder().run {
            val list = value.split("_")
            if (list.size == 1) {
                val item = list[0]
                if (item.length > 1) {
                    append(item.substring(0, 1).toUpperCase())
                    append(item.substring(1))
                } else {
                    append(item.toUpperCase())
                }
            } else {
                for (item in list) {
                    if (item.length < 2) {
                        append(item.toUpperCase())
                    } else {
                        append(item.substring(0, 1).toUpperCase())
                        append(item.substring(1).toLowerCase())
                    }
                }
            }
            toString()
        }
    }

    @Test
    fun test() {
        val str = "list_item"
        val str2 = str.toCamelCase()
        println(str2)
        val str3 = "listItem"
        val str4 = str3.toCamelCase()
        println(str4)
    }
}