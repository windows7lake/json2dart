import com.lake.json2dart.config.ProjectConfig
import com.lake.json2dart.generator.DartClassCodeMaker
import com.lake.json2dart.generator.DartClassMaker
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class Test001 {

    private val rawJson = """{
  "id": 1441,
  "parent_id": null,
  "name": "New World",
  "link": "https://www.xxxxxx.com",
  "date_created": "2018-11-12T10:51:24",
  "date_created_gmt": "2018-11-12T10:51:24",
  "date_modified": "2018-12-04T06:35:46",
  "date_modified_gmt": "2018-12-04T06:35:46",
  "data": {
    "data1": 111,
    "data2": "222",
    "data3": 33.33,
    "meta": {
        "total": 1
    }
  },
  "list": [
    {
        "item1": "1111",
        "item2": 2222
    }
  ],
  "map": {
        "map_item1": "1111",
        "map_item1": 2222
    }
}"""

    private val rawJson2 = """
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 10484,
        "name": "淼栩設計",
        "company_full_name": "淼栩空間設計",
        "logo_url": "https://images.debug.100.com.tw/images/companies/202107/09/api_2056972_1625795135_Gzx9dg15EZ.jpg!t180.jpg",
        "design_cost_cn": "4000元/坪起",
        "all_interact_count": 69,
        "message_count": 7,
        "style_cn": [
            "現代風",
            "北歐風",
            "混搭風",
            "日式風"
        ],
        "display_construction_region": "無、基隆市",
        "service_points_cn": [
            "承接局部預算",
            "一年內保固",
            "接客變案",
            "簽訂合約保障"
        ],
        "display_construction_val_cn": "萬/坪",
        "concept_remark": null,
        "service_item_cn": [
            "景觀設計"
        ],
        "service_region_cn": [
            "新竹縣"
        ],
        "sample_room_cn": "",
        "cost_cn": "222萬起",
        "part_cost_cn": "1萬起",
        "measure_cost_cn": "面議",
        "stage_payment_cn": [
            {
                "name": "2",
                "payment_rate_cn": "合同約定總額的2%"
            },
            {
                "name": "2",
                "payment_rate_cn": "剩餘所有尾款一次結清"
            }
        ],
        "new_service_process_cn": [
            {
                "id": 1,
                "title": "免費諮詢：溝通需求&風格",
                "children": [
                    {
                        "id": 100,
                        "description": "",
                        "title": "設計師1對1溝通"
                    }
                ],
                "short_title": "傾聽需求"
            }
        ],
        "movie_url": "https://images.debug.100.com.tw/",
        "warranty_period_cn": "一年內保固",
        "construction_val_cn": [
            {
                "kind": "新成屋",
                "price": ""
            },
            {
                "kind": "預售屋",
                "price": ""
            },
            {
                "kind": "中古屋",
                "price": ""
            },
            {
                "kind": "商業空間",
                "price": ""
            },
            {
                "kind": "老屋翻新",
                "price": ""
            }
        ],
        "company_no": "83302261",
        "posttime": "2021-07-08 03:35:37",
        "company_size_cn": "",
        "service_time_cn": {
            "start_day": "星期一",
            "end_day": "星期五",
            "start_time": "09:30",
            "end_time": "18:00"
        },
        "has_im": 1,
        "line_me": "",
        "line": "",
        "facebook": "https://www.facebook.com/%E6%B7%BC%E6%A0%A9%E7%A9%BA%E9%96%93%E8%A8%AD%E8%A8%88-105867441410936",
        "fb_share_url": "/10484?utm_source=mobile&utm_medium=qrcode_facebook",
        "line_share_url": "/10484?utm_source=mobile&utm_medium=qrcode_line",
        "copy_url": "/10484?utm_source=mobile&utm_medium=copy_url",
        "is_open_phone": 1,
        "is_open_message": 1,
        "hot_works": [
            {
                "id": 11603,
                "name": "綠光森林NO.22",
                "open_params": {
                    "page_type": 202,
                    "page_id": 11603
                },
                "cover_img": {
                    "id": 246285,
                    "url": "https://images.debug.100.com.tw/images/works/202111/01/api_2056972_1635760912_y4QemPqJ5K.jpg!t550.jpg",
                    "aspect_ratio": 1.59114
                }
            },
            {
                "id": 11240,
                "name": "金榮朝代",
                "open_params": {
                    "page_type": 202,
                    "page_id": 11240
                },
                "cover_img": {
                    "id": 234658,
                    "url": "https://images.debug.100.com.tw/images/works/202109/07/api_2056972_1630997743_aSaNFDWSsl.jpg!t550.jpg",
                    "aspect_ratio": 1.50281
                }
            },
            {
                "id": 12173,
                "name": "豐邑晴空匯",
                "open_params": {
                    "page_type": 202,
                    "page_id": 12173
                },
                "cover_img": {
                    "id": 262216,
                    "url": "https://images.debug.100.com.tw/images/works/202205/24/api_2056972_1653383422_Pc8bmlV5aS.jpg!t550.jpg",
                    "aspect_ratio": 0.66667
                }
            }
        ],
        "designers": [],
        "honors": [],
        "background_images": [
            {
                "id": 193345,
                "url": "https://images.debug.100.com.tw/images/companies/202107/08/api_2056972_1625715280_OxCK6m4V37.jpg!t550.jpg",
                "aspect_ratio": 1
            }
        ],
        "addresses": [
            {
                "id": 5379,
                "region": "新竹縣",
                "section": "竹北市",
                "street_name": "光明六路",
                "full_address": "新竹縣竹北市光明六路825號",
                "latlng": "24.8331439,120.9999106",
                "distance": -1
            }
        ],
        "number": {
            "id": 30013,
            "the_id": 10484,
            "host_tel": "0986-851-001",
            "number": "3031958",
            "contact": "0986851001,3031958",
            "tel_type": "mobile",
            "the_type": 1,
            "is_show": true
        },
        "numbers": [
            {
                "id": 30012,
                "the_id": 10484,
                "host_tel": "0986-851-001",
                "number": "3031957",
                "contact": "0986851001,3031957",
                "tel_type": "tel",
                "the_type": 1,
                "is_show": true
            },
            {
                "id": 30013,
                "the_id": 10484,
                "host_tel": "0986-851-001",
                "number": "3031958",
                "contact": "0986851001,3031958",
                "tel_type": "mobile",
                "the_type": 1,
                "is_show": true
            }
        ],
        "is_collect": false,
        "near_address": {
            "id": 5379,
            "region": "新竹縣",
            "section": "竹北市",
            "street_name": "光明六路",
            "full_address": "新竹縣竹北市光明六路825號",
            "latlng": "24.8331439,120.9999106",
            "distance": -1
        }
    },
    "meta": {
        "pagination": [],
        "extra": {
            "tabs": [
                {
                    "name": "全部",
                    "show": 1,
                    "type": "work",
                    "search_field": {
                        "filter_show": 1
                    }
                },
                {
                    "name": "代表作",
                    "type": "work",
                    "show": true,
                    "search_field": {
                        "filter_represent": 1
                    }
                },
                {
                    "name": "北歐風",
                    "show": 1,
                    "search_field": {
                        "filter_style": 12
                    }
                },
                {
                    "name": "工業風",
                    "show": 1,
                    "search_field": {
                        "filter_style": 13
                    }
                },
                {
                    "name": "現代風",
                    "show": 1,
                    "search_field": {
                        "filter_style": 1
                    }
                },
                {
                    "name": "大坪數",
                    "type": "work",
                    "show": true,
                    "search_field": {
                        "filter_big_size": 1
                    }
                },
                {
                    "name": "老屋",
                    "type": "work",
                    "show": true,
                    "search_field": {
                        "filter_kind": 3
                    }
                }
            ]
        }
    }
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
    fun test001() {
        val dartClass = DartClassMaker("Test", rawJson).makeDartClass()
        val result = DartClassCodeMaker(dartClass).makeDartClassCode()
        println(result)
    }
}