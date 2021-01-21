require: catchAll/catchAll.sc

require: city/cities-ru.csv
    module = common
    name = cities
    var = $cities
    
patterns:
    $City = $entity<cities>

theme: /

    state: 
        q!: $regex</start>
        a: Начнём.

    state: привет
        intent!: /привет
        a: Привет привет

    state: пока
        intent!: /пока
        a: Пока пока
        
    state: City
        q!: $City
        a: {{toPrettyString($parseTree)}}
        script:
            var city_id = $parseTree.City[0].value;
            $temp.value = $cities[city_id].value;
        a: {{toPrettyString($temp.value)}}

