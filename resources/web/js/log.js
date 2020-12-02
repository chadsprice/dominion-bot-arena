function message(text, indent) {
  const p = $('<p>', {html: text});
  if (indent === 0) {
    $('#log').append(p);
    return;
  }
  while (logIndents.length - 1 < indent) {
    const div = $('<div>', {'class': 'logIndent'});
    logIndents[logIndents.length - 1].append(div);
    logIndents.push(div);
  }
  while (logIndents.length - 1 > indent) {
    logIndents.pop();
  }
  logIndents[indent].append(p);
}

function newTurnMessage(text) {
  const div = $('<div>', {'class': 'turnIndent'}).append(
    $('<p>', {html: text})
  );
  $('#log').append(div);
  logIndents = [div];
}
