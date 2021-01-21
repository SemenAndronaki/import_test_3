require: emotionClassifier.sc
require: catchAllClassifier.js
require: checkSameAnswer.js
require: answer.js
require: answers.yaml
    var = zwitch

patterns:
    $catchAll = *
    $yes = [ну] [конечно|всё|все|вроде|пожалуй|возможно] (да|даа|lf|ага|агась|точно|угу|верно|ок|ok|окей|окай|okay|оке|именно|подтвержд*|йес) [да|конечно|конешно|канешна|всё|все|вроде|пожалуй|возможно]

    $agree = ({[$yes|конечно|конешно|канешна] (* $agreeStrong *|$agreeWeak)}|$repeat<$yes>)

    $agreeWeak = (давай|давайте|логично|могу|было дело|бывало|бывает)

    $agreeStrong = (конечно|конешно|канешна|а то [нет]|очень|[ты] прав*|абсолютно|обязательно|непременно|а как же|[я] подтверждаю|[совершенно|абсолютно] точно|пожалуй|запросто|норм|(почему/что/че) [бы] [и] нет|хочу|было [бы] (неплохо|не плохо)|[я] [очень] (хочу|хо чу|ладно|хорошо)|ладно|можно|валяй*|договорились|[я] [совершенно|абсолютно] [с (тобой/вами)] согла*|не могу [с (тобой/вами)] не согласиться|вполне|в полной мере|естественно|разумеется|(еще|ещё) как|[я] не (против|возражаю|сомневаюсь)|я только за|безусловн*|[это] так [и есть]|все (так|верно)|(совершенно|абсолютно) верно)

    $no = (нет|неат|ниат|неа|ноуп|ноу|найн) [нет] [спасибо]

    $disagree = (* $disagreeStrong *|$disagreeWeak|$no)
    
    $disagreeWeak = (да ну|не|нее|ничего)

    $disagreeStrong = [$no|конечно|конешно|канешна] ($no|не сейчас/ни капли/отнюдь/нискол*/да ладно/[я] не (хоч*|хо чу|надо|могу|очень|думаю|нравится|стоит|буду|считаю|согла*|подтв*)|ненадо|нельзя|нехочу|ненавижу|невозможно|никогда|никуда|ни за что|нисколько|никак*|никто|ниразу|[я] против|вряд ли|сомневаюсь|нихрена|неправильно|неверно|невсегда|[это] {не так}|отказываюсь|ни в коем случае) [(конечно|конешно|канешна|спасибо)]


theme: /
    init:
        $global.catchAll = {
            //количество попаданий в CatchAll, после которых бот предлагает оператора или говорит, что вопрос не в компетенции.
            giveUpRepetition: $injector.giveUpRepetition || 2,

            //темы, которые озвучивает бот при попадании в CatchAll
            topics: $injector.topics,

            //нужно ли просить переформулировать, если бот хочет снова ответить то же самое.
            CheckSameAnswer: $injector.CheckSameAnswer || false,

            //есть ли перевод на оператора
            withOperator: $injector.withOperator || false,

            //стейт, в который переходит бот после того, как завершился чат с оператором            
            livechatFinished: $injector.livechatFinished,

            //массив фраз, по которым выходит из диалога с оператором
            closeChatPhrases: $injector.closeChatPhrases || ["/close"],

            //группа операторов, которым будут приходить сообщения. По умолчанию приходит всем.
            operatorGroup: $injector.operatorGroup
        };

    state: CatchAll         || noContext = true
        event!: noMatch
        a: {{ toPrettyString($parseTree) }}
        script:
            $session.catchAll = $session.catchAll || {};

            //Начинаем считать попадания в кэчол с нуля, когда предыдущий стейт не кэчол.
            if ($session.lastState && !$session.lastState.startsWith("/CatchAll")) {
                $session.catchAll.repetition = 0;
            } else{
                $session.catchAll.repetition = $session.catchAll.repetition || 0;
            }

            // увеличиваем счётчик входов в catchAll
            $session.catchAll.repetition += 1;

            // определяем класс
            var clazz = catchAllClassifier.check($parseTree);

            
            $reactions.answer(toPrettyString(clazz));

        state: SeemsMeaningful || noContext = true 
            if: $session.catchAll.repetition <= catchAll.giveUpRepetition
                if: catchAll.topics
                    script:
                        $reactions.answer(getAnswer('SeemsMeaningfulWithTopics'));
                else: 
                    script:
                        $reactions.answer(getAnswer('SeemsMeaningful'));
            else:
                go!: ../OutOfScope

        #Этот стейт нужен, чтобы не переводить бессмыслицу на оператора.
        state: Nonsense || noContext = true 
            if: catchAll.topics
                script:
                    $reactions.answer(getAnswer('NonsenseWithTopics'));
            else: 
                script:
                    $reactions.answer(getAnswer('Nonsense'));

        state: Transliteration
            a: {{toPrettyString($nlp.match($session.catchAll.transliterationText, "/"))}}
            script:
                $reactions.answer(getAnswer('Transliteration'));

            state: Yes
                q: $agree       || onlyThisState = true
                go!: {{ $session.catchAll.transliterationState }}

            state: NoUnknown
                q: $disagree    || onlyThisState = true
                go!: /CatchAll/AskAgain?


        state: NegativeEmotion  || noContext = true 
            if: catchAll.topics
                script:
                    $reactions.answer(getAnswer('NegativeEmotionWithTopics'));
            else: 
                script:
                    $reactions.answer(getAnswer('NegativeEmotion'));

        state: SameAnswer || noContext = true
            script:
                $reactions.answer(getAnswer('SameAnswer')); 


        state: AskAgain?
            script:
                $reactions.answer(getAnswer('AskAgain?')); 

        state: OutOfScope  || noContext = true
            script:
                $reactions.answer(getAnswer('OutOfScope')); 
            if: (catchAll.withOperator && hasOperatorsOnline(catchAll.operatorGroup)) || (catchAll.withOperator && testMode())
                go!: /Switch/DoYouWannaSwitch?



