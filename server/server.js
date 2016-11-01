var express = require('express')
var app = express();
var fs = require('fs');
var port = 8080;
var scoresFile = "scores.json";
var scores;
var maxHighScore = 5;

app.get('/', function(req, res) {
  res.json({message: "PacMan Server REST API"})
})

app.get('/score', function(req, res) {
    res.json(scores);
})

app.get('/score/:name/:score', function(req, res) {
    var obj = {
        score: req.params.score,
        name: req.params.name,
        date: new Date().getTime()
    }

    var inserted = false;

    for(var i = scores.length - 1; i>=0; i--) {
        if(obj.score > scores[i].score) {
            scores.splice(i + 1, 0, obj);
            inserted = true;
            break;
        }
    }

    if(!inserted)
        scores.unshift(obj);

    if(scores.length > maxHighScore)
        scores.splice(0, 1);

    var isNewScore = false;
    for(var i = 0; i<scores.length; i++) {
        if(scores[i].score == obj.score &&
            scores[i].date == obj.date &&
            scores[i].name == obj.name)
            isNewScore = true;
    }

    res.json(isNewScore);
})

app.listen(port, function() {
    console.log("Server started on port" + port);
    scores = JSON.parse(fs.readFileSync(scoresFile, 'utf8'));
    console.log("High scores loaded from file. Total: " + scores.length);
});


process.on('SIGINT', function() {
    fs.writeFileSync(scoresFile, JSON.stringify(scores));
    process.exit();
});