{
<#if dataMap??>
    "svgList":[
        <#if (dataMap.svgList?size>0)>
            <#list dataMap.svgList as svgListMap>
                {
                    "id":"${svgListMap.id!''}",
                    "name":"${svgListMap.name!''}",
                    "titleList":{
                         "titleClassId":"${svgListMap.titleList.titleClassId!''}",
                         "titleClassName":"${svgListMap.titleList.titleClassName!''}",
                         "list":[
                              <#if (svgListMap.titleList.list?size>0)>
                                   <#list svgListMap.titleList.list as list>
                                        {
                                             "titleId":"${list.titleId!''}",
                                             "titleName":"${list.titleName!''}",
                                             "titleUrl":"${list.titleUrl!''}",
                                             "flag":"${list.flag!''}"
                                        }<#if list_has_next>,</#if>
                                   </#list>
                              <#else>
                                   {
                                        "titleId":"",
                                        "titleName":"",
                                        "titleUrl":"",
                                        "flag":""
                                   }
                              </#if>
                         ]
                    }
                }<#if svgListMap_has_next>,</#if>
            </#list>
        <#else>
            {
                "id":"",
                "name":"",
                "titleList":{
                    "titleClassId":"",
                    "titleClassName":"",
                    "list":[
                        {
                            "titleId":"",
                            "titleName":"",
                            "titleUrl":"",
                            "flag":""
                        }
                    ]
                }
            }
        </#if>
    ]
<#else>
</#if>
}