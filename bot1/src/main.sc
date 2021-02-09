require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Начнём. bot1

    state: Hello
        intent!: /привет
        a: Привет привет bot1

    state: Bye
        intent!: /пока
        a: Пока пока bot1

    state:
        q!: @ClientEntity
        a: А теперь сущность распозналась: {{$parseTree._ClientEntity}}

    state:
        event!: noMatch
        a: Сработал no match. То, что ты написал, будет добавлено в сущность. Напиши введённое сообщение во второй раз и оно попадёт в другой стейт.
        script:
            $caila.addClientEntityRecords("ClientEntity", [{"type": "synonyms", "rule": [$parseTree.text], "value": $parseTree.text}]);

