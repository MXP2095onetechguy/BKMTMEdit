const require = function(location){
	var objec = null;

	if(typeof window === 'undefined'){
		try{
			objec = require(location);
		}
		catch(e){
			objec = import(location);
		}
	}
	else{
		let scriptn = document.createElement("script");
		scriptn.src = location;
		document.body.appendChild(scriptn);
	}
	
	return objec;
}