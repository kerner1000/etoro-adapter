const url = "http://localhost:7070/open-positions-grouped-bysector";
const colors = [];

function grab() {
    /* Promise to make sure data loads */
    return new Promise((resolve, reject) => {
        $.ajax({
            url: url,
            method: "GET",
            dataType: 'JSON',
            success: function (data) {
                resolve(data)
            },
            error: function (error) {
                reject(error);
            }
        })
    })
}

$(document).ready(function () {
    grab().then((jsonData) => {
        console.log('Received json data: %j', jsonData);
        let labels = [];
        let values = [];

        try {
            jsonData.elements.forEach((item) => {
                labels.push(item.groupIdentifier);
                values.push(item.amount);
            });

            labels.forEach((element) => {
                colors.push(getRandomColorHex());
            });

            const valueNumbers = values.map(v => Number(v));
            const valueSum = valueNumbers.reduce((a, b) => a + b, 0);
            const slices = valueNumbers.map((v, i) => ({label: labels[i], value: v}))
                .reduce((accumulator, currObj) => {
                    const percent = 100 * currObj.value / valueSum;
                    if (percent < thresholdPercentGroupingOthers) {
                        const others = accumulator.find(o => o.label === 'Others');
                        if (!others) {
                            return accumulator.concat({label: 'Others', value: currObj.value});
                        }
                        others.value += currObj.value;
                    } else {
                        accumulator.push(currObj);
                    }
                    return accumulator;
                }, []);

            let chartdata = {
                labels: slices.map(o => o.label),
                datasets: [{
                    label: 'S1',
                    backgroundColor: colors,
                    data: slices.map(o => o.value)
                }]
            };

            let ctx = $("#myChart");

            var barGraph = new Chart(ctx, {
                type: 'doughnut',
                data: chartdata,
                options: {
                    title: {
                        display: true,
                        text: 'Sector Breakdown'
                    },
                    tooltips: {
                        enabled: true
                    },
                    legend: {
                        position: "left"
                    },
                    plugins: {
                        labels: {
                            precision: 2,
                            arc: false,
                            position: 'default',
                            fontColor: '#fff',
                        }
                    },
                    layout: {
                        padding: 10
                    },
                    'onClick': function (evt, item) {
                        let activePoints = barGraph.getElementsAtEvent(evt);
                        if (activePoints[0]) {
                            let chartData = activePoints[0]['_chart'].config.data;
                            let idx = activePoints[0]['_index'];
                            let label = chartData.labels[idx];
                            let value = chartData.datasets[0].data[idx];

                            jQuery('#tableHeading').html(label);

                            extractDataAndWriteTable(jsonData, label);
                        }
                    }
                }
            });

        } catch (error) {
            console.log('Error parsing JSON data', error)
        }

    }).catch((error) => {
        console.log(error);
    })
});




