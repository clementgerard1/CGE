package module;

import java.util.ArrayList;
import java.util.Hashtable;

import com.cycling74.max.Atom;
import com.cycling74.max.MaxBox;
import com.cycling74.max.MaxObject;
import com.cycling74.max.MaxPatcher;

import tools.Datas;
import tools.Tools;

public class ModulePatch {

	private MaxPatcher parentPatcher;
	private MaxPatcher  modulePatch;
	public String modulePatchId;
	private MaxBox modulePatchBox;
	public MaxBox toModule;
	public String toModuleId;
	
	String id;
	private int inlets;
	private int outlets;
	private int sizeX;
	private int sizeY;
	
	public ModulePatch(MaxPatcher parentPatcher, String modulePatchId, String toModuleId, Module parent){
		this.id = parent.getId();
		this.inlets = parent.inlets;
		this.outlets = parent.outlets;
		this.sizeX = parent.sizeX;
		this.sizeY = parent.sizeY;
		this.parentPatcher = parentPatcher;
		this.modulePatchId = modulePatchId;
		this.toModuleId = toModuleId;
		this.modulePatchBox = parentPatcher.getNamedBox(modulePatchId);
		this.modulePatch = this.modulePatchBox.getSubPatcher();
		this.toModule = parentPatcher.getNamedBox(toModuleId);
	}
	
	public MaxPatcher getModulePatch() {
		return modulePatch;
	}

	public void setModulePatch(MaxPatcher modulePatch) {
		this.modulePatch = modulePatch;
	}

	public MaxBox getModulePatchBox() {
		return modulePatchBox;
	}

	public void setModulePatchBox(MaxBox modulePatchBox) {
		this.modulePatchBox = modulePatchBox;
	}

	public int getSizeX() {
		return sizeX;
	}

	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}

	public ModulePatch(MaxPatcher parent, String id, String path){
		this.id = id;
		String[] pathPatch = {Tools.newPatch("modulePatch", path), "@presentation", "1"};
		this.modulePatchId = Datas.getCgeId();
		modulePatchBox = parent.newDefault(Module.sliderEntreeSize, 20, "bpatcher", Atom.newAtom(pathPatch));
		modulePatchBox.setName(this.modulePatchId);
		modulePatch = modulePatchBox.getSubPatcher();
		parentPatcher = parent;
		
		// Link avec les autres modules
		Hashtable<String, Object> infos = Tools.determinInfos(modulePatch);
 		inlets = (Integer) infos.get("inlets");
		outlets = (Integer) infos.get("outlets");
		sizeX = (Integer) infos.get("sizeX");
		sizeY = (Integer) infos.get("sizeY");
		Tools.move(modulePatchBox, Module.sliderEntreeSize, 20 , sizeX, sizeY);
		ArrayList<MaxBox> receive = new ArrayList<MaxBox>();
		ArrayList<MaxBox> send = new ArrayList<MaxBox>();
		for(int i = 0 ; i < inlets ; i++){
			String[] receiveArgs = {id + "-inPatch-" + i};
			receive.add(parentPatcher.newDefault(Module.sliderEntreeSize, 20 * i, "receive~", Atom.newAtom(receiveArgs)));
			parentPatcher.connect(receive.get(i), 0, modulePatchBox, i);
		}
		String pathEnvoi = Tools.newPatch("moduleSend");
		pathEnvoi = pathEnvoi.split("/")[pathEnvoi.split("/").length-1];
		String[] receiveArgss = {id};
		this.toModuleId=Datas.getCgeId();
		this.toModule = parentPatcher.newDefault(Module.sliderEntreeSize, 50, pathEnvoi, Atom.newAtom(receiveArgss));
		this.toModule.setName(this.toModuleId);
		for(int i = 0 ; i < outlets ; i++){
			String[] receiveArgs = {id + "-outPatch-" + i};
			send.add(parentPatcher.newDefault(Module.sliderEntreeSize, 100 + (20 * i), "send~", Atom.newAtom(receiveArgs)));
			parentPatcher.connect(modulePatchBox, i, send.get(i), 0);
			if(i==1){
				parentPatcher.connect(modulePatchBox, i, toModule, 1);
			}else{
				parentPatcher.connect(modulePatchBox, i, toModule, 0);
			}
		}
		//MaxBox configButton = parentPatcher.getNamedBox("CGEcorpsConfig");
		MaxBox midiButton = parentPatcher.getNamedBox("CGEcorpsMidiLearn");
		
		//On actualise les boutons
		//Tools.move(configButton, 0, 0, Module.sliderEntreeSize, 20);
		if(inlets > 0 && outlets > 0){
			Tools.move(midiButton, 0, 0, sizeX + Module.sliderEntreeSize + Module.sortieSize, 20);
		}else if (inlets == 0 && outlets > 0){
			Tools.move(midiButton, 0, 0, sizeX + Module.sortieSize, 20);
		}else if (inlets > 0 && outlets == 0){
			Tools.move(midiButton, 0, 0, sizeX + Module.sliderEntreeSize, 20);
		}else{
			Tools.move(midiButton, 0, 0, sizeX, 20);
		}
		
		
		//On ajoute les patchs de midiLearning
		int i = 0;
		for(MaxBox box : modulePatch.getAllBoxes()){
			Tools.midiLearn(box);
		}
	}

	public int getInlets() {
		return inlets;
	}

	public void setInlets(int inlets) {
		this.inlets = inlets;
	}

	public int getOutlets() {
		return outlets;
	}

	public void setOutlets(int outlets) {
		this.outlets = outlets;
	}
	
}
