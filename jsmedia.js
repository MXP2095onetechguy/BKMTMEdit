function audioPlayer(src){
	this.sound = document.createElement("audio");
	this.sound.src = src;
	this.sound.setAttribute("preload", "auto");
	this.sound.setAttribute("controls", "none");
	this.sound.style.display = "none";
	document.body.appendChild(this.sound);

	this.setTime = function(time){
		this.sound.currentTime = time;
	}

	this.getTime = function(){
		return this.sound.currentTime;
	}

	


	this.getSound = function(){
		return this.sound;
	}


	

	this.setSource = function(src){
		this.sound.src = src;
	}

	this.getSource = function(){
		return this.sound.src;
	}
	
	

	this.play = function(){
		this.sound.play();
	}

	this.pause = function(){
		this.sound.pause();
	}

	this.stop = function(){
		this.pause();
		this.setTime(0);
	}
}