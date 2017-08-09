{
"data":[
    <#if dataList?? && (dataList?size>0)>
        <#list dataList as data>
            <#if ${data.markType} == "1">
            {
                "ord":"${data.ord!'-'}",
                "markType":"${data.markType!'-'}",
                "id":"${data.id!'-'}",
                "isMinus":"${data.isMinus!'-'}",
                "isPercentage":"${data.isPercentage!'-'}",
                "url":"${data.url!'-'}",
                "data":${data.data}
            }
            <#if ${data.markType} == "2">
            {
                "ord":"${data.ord!'-'}",
                "id":"${data.id!'-'}",
                "url":"${data.url!'-'}",
                "markType":"${data.markType!'-'}",
                "data":${data.data}
            }
            </#if>
            <#if data_has_next>,</#if>
        </#list>
    <#else>

    </#if>
]
}