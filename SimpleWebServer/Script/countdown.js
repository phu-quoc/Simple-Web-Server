

    var thptExam = new Date(2021, 6, 7, 00, 00 ,00, 00).getTime();
    var countDown = setInterval(run,1000);
    function run()
    {
        var now = new Date().getTime();
        var count = thptExam - now;
        var days = Math.floor(count/(24*60*60*1000));
        var hours = Math.floor(count%(24*60*60*1000)/(60*60*1000));
        var minutes = Math.floor(count%(60*60*1000)/(60*1000));
        var seconds = Math.floor(count%(60*1000)/1000);
        document.getElementById("days").innerHTML = days;
        document.getElementById("hours").innerHTML = hours; 
        document.getElementById("minutes").innerHTML = minutes; 
        document.getElementById("seconds").innerHTML = seconds;  
    }

  
    
