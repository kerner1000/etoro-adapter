const thresholdPercentGroupingOthers = 3;

function getRandomColorHex() {
    let hex = "0123456789ABCDEF",
        color = "#";
    for (var i = 1; i <= 6; i++) {
        color += hex[Math.floor(Math.random() * 16)];
    }
    return color;
}

function dataToTable(tableElements) {

    tableElements.sort(compareTableElements);

    let html = '<table>';
    let i;
    for (i = 0; i < tableElements.length; i++) {
        label2 = tableElements[i].label;
        html += '<tr>' + '<td>' + label2 + '</td>' + '<td  align=\'right\' >' + (Math.round(tableElements[i].number * 100) / 100).toFixed(2) + '</td></tr>';
    }
    return html + '</table>';
}

function compareTableElements(a, b) {
    if (a.number < b.number) {
        return 1;
    }
    if (a.number > b.number) {
        return -1;
    }
    return 0;
}

class TableElement {
    constructor(label, number) {
        this.label = label;
        this.number = number;
    }
}

function extractDataAndWriteTable(jsonData, label) {
    let tableElements = [];
    jsonData.elements.forEach((item) => {
        if (item.groupIdentifier === label) {
            item.elements.forEach((item2) => {
                item2.taxonomies.taxonomies.forEach((item3) => {
                    // console.log(item3.identifier);
                    let label4;
                    if (item3.identifier === 'name') {
                        if (item3.value === 'na') {
                            label4 = simplifyTicker(extractTickerFromInstrument(item2.instrument));
                        } else {
                            label4 = simplifyTicker(extractTickerFromInstrument(item2.instrument)) + " (" + item3.value + ")";
                        }
                        tableElements.push(new TableElement(label4, item2.amount))
                    }
                })
            })
        }
    });
    writeTable(tableElements);
}

function extractTickerFromInstrument(instrument) {
    if (instrument.includes('/')) {
        return instrument.substring(0, instrument.indexOf('/'))
    }
    return instrument;
}

function simplifyTicker(ticker) {
    if (ticker.includes('.')) {
        return ticker.substring(0, ticker.indexOf('.'))
    }
    return ticker;
}

function writeTable(tableElements) {

    jQuery('#table').html(dataToTable(tableElements));

}