{
"data":[
    <#if dataList?? && (dataList?size>0)>
        <#list dataList as data>
            <#if "${data.markType}" == "1">
            {
                "ord":"${data.ord!'-'}",
                "markType":"${data.markType!'-'}",
                "id":"${data.id!'-'}",
                "isMinus":"${data.isMinus!'-'}",
                "isPercentage":"${data.isPercentage!'-'}",
                "url":"${data.url!'-'}",
                "data":${data.data}//TODO
            }
            <#elseif "${data.markType}" == "2">
            {
                "ord":"${data.ord!'-'}",
                "id":"${data.id!'-'}",
                "url":"${data.url!'-'}",
                "markType":"${data.markType!'-'}",
                "data":<#if data.data??>
                {
                    "date":"${data.data.date!'-'}",
                    "dayOrMonth":"${data.data.dayOrMonth!'-'}",
                    "markName":"${data.data.markName!'-'}",
                    "chartType":"${data.data.chartType!'-'}",
                    "title":"${data.data.title!'-'}",
                    "chart"://TODO
                }

                ${data.data}
            }
            </#if>
            <#if data_has_next>,</#if>
        </#list>
    <#else>

    </#if>
]
}