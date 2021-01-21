function getAnswer(answer){
    if(typeof(specialSwitch) != 'undefined'){
        return selectRandomArg(specialSwitch[answer]);
    }
    return selectRandomArg(zwitch[answer])
}

function selectRandomArg() {
    var arg = arguments;
    if (Array.isArray(arg[0]) && arg.length === 1) {
        arg = arg[0];
    }
    var index;
    if (testMode()) {
        index = 0;
    } else {
        index = $reactions.random(arg.length);
    }
    return arg[index];
}

function testMode() {
    if ($jsapi.context().testContext) {
        return true;
    } else {
        return false;
    }
}