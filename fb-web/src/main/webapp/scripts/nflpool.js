var unordered = 1;

function getGamePick(game) {
    for (var i=0; i<game.length; i++) {
        if (game[i].checked == true) {
            return game[i].value;
        }
    }
}

function setGamePick(games, pick) {
    for (var j=0; j<games.length; j++) {
        game = games[j];
        for (var i=0; i<game.length; i++) {
            if (game[i].value == pick) {
                game[i].checked = true;
            }
        }
    }
}

function moveUp(pbox) {
    unordered = 0;
    for (var i=1; i<pbox.options.length; i++) {
        if (pbox.options[i].selected) {
            pbox.options[i].selected = false;
            pbox.options[i-1].selected = true;
            tmpValue = pbox.options[i-1].value;
            tmpText = pbox.options[i-1].text;
            pbox.options[i-1].value = pbox.options[i].value;
            pbox.options[i-1].text = pbox.options[i].text;
            pbox.options[i].value = tmpValue;
            pbox.options[i].text = tmpText;
            pbox.options[i].selected = false;
            pbox.options[i-1].selected = true;
            return;
        }
    }
}

function moveDown(pbox) {
    unordered = 0;
    for (var i=pbox.options.length-2; i>=0; i--) {
        if (pbox.options[i].selected) {
            pbox.options[i].selected = false;
            pbox.options[i+1].selected = true;
            tmpValue = pbox.options[i+1].value;
            tmpText = pbox.options[i+1].text;
            pbox.options[i+1].value = pbox.options[i].value;
            pbox.options[i+1].text = pbox.options[i].text;
            pbox.options[i].value = tmpValue;
            pbox.options[i].text = tmpText;
            pbox.options[i].selected = false;
            pbox.options[i+1].selected = true;
            return;
        }
    }
}

function changePick(game, pbox) {
    var newPick;
    var oldPick;
    if (game[0].checked == true) {
        newPick = game[0].value;
        oldPick = game[1].value;
    }
    if (game[1].checked == true) {
        newPick = game[1].value;
        oldPick = game[0].value;
    }
    for (var i=0; i<pbox.options.length; i++) {
		if (pbox.options[i].value == oldPick) {
			pbox.options[i].value = newPick;
			pbox.options[i].text = newPick;
			pbox.options[i].selected = true;
		}
        else {
			pbox.options[i].selected = false;
        }
    }
}

function str2array(aData) {
  var numVars = 1;
  var start = aData.indexOf("!") + 1;
  while (start != 0) {
    numVars++;
    start = aData.indexOf("!", start) + 1;
  }
  if (numVars == 0) {
      return null;
  }
  var data = new blankArray(numVars);
  for (var i=0; i < data.length; i++) {
    var end = aData.indexOf("!", start);
    if (end == -1) end = aData.length;
    data[i] = aData.substring(start, end);
    start = end + 1;
  }
  return data;
}

function getCookie(name) {
  var index = document.cookie.indexOf(name + "=");
  if (index == -1) return null;
  index = document.cookie.indexOf("=", index) + 1;
  var endstr = document.cookie.indexOf(";", index);
  if (endstr == -1) endstr = document.cookie.length;
  return unescape(document.cookie.substring(index, endstr));
}


function restorePicks(entryStr) {
    if (entryStr != null) {
        var lastPicks = str2array(entryStr);
        unordered = 0;
    }

    for (var i=0; i<games.length; i++) {
        if (lastPicks != null) {
            document.NFLPool.picks.options[i].value = lastPicks[i];
            setGamePick(games, document.NFLPool.picks.options[i].value);
        }
        else {
            document.NFLPool.picks.options[i].value = getGamePick(games[i]);
        }
        document.NFLPool.picks.options[i].text =
            document.NFLPool.picks.options[i].value;
    }
}
