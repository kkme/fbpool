var thiscookie = document.cookie;

function getCookie(name) {
  var index = document.cookie.indexOf(name + "=");
  if (index == -1) return null;
  index = document.cookie.indexOf("=", index) + 1;
  var endstr = document.cookie.indexOf(";", index);
  if (endstr == -1) endstr = document.cookie.length;
  return unescape(document.cookie.substring(index, endstr));
}

var today = new Date();
var expiry = new Date(today.getTime() + 300 * 24 * 60 * 60 * 1000);

function setCookie(name) {
  document.cookie = eval('"' + name + '=" + escape(' + name + ') + "; expires=' + expiry.toGMTString() + '"');
  thiscookie = document.cookie;
}

function blankArray(arrayLength) {
  this.length = arrayLength;
  for (var i=0; i < this.length; i++)
    this[i] = "";
}

function cookie2array(aData) {
  var numVars = 1;
  var start = aData.indexOf("!") + 1;
  while (start != 0) {
    numVars++;
    start = aData.indexOf("!", start) + 1;
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

function defineCookie(newData) {
  userData = newData[0];
  for (var i=1; i < newData.length; i++)
    userData += "!" + newData[i];
  setCookie("userData");
}
