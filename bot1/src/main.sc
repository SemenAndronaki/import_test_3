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

    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}} bot1

