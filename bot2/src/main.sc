require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Начнём bot2

    state: Hello
        intent!: /привет
        a: Привет привет bot2

    state: Bye
        intent!: /пока
        a: Пока пока bot2

    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}} bot2

