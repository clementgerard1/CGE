package module;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.UUID;

import com.cycling74.max.Atom;
import com.cycling74.max.MaxBox;
import com.cycling74.max.MaxObject;
import com.cycling74.max.MaxPatcher;

import tools.Datas;
import tools.Tools;

public class ModuleEntree {

	private MaxPatcher parentPatcher;
	private MaxPatcher  moduleEntreePatch;
	public String moduleEntreePatchBoxId;
	private MaxBox moduleEntreePatchBox;
	public String moduleEntreeTabBoxId;
	private MaxBox moduleEntreeTabBox;
	public String moduleEntreeSliderCorpsBoxId;
	private MaxBox moduleEntreeSliderCorpsBox;
	private MaxPatcher moduleEntreeSliderCorpsPatcher;
	private String pattrId;
	String id;
	int inlets;
	private Hashtable<Integer,Hashtable<Integer, MaxBox>> patchs; 
	public Hashtable<Integer,Hashtable<Integer,String>> patchsId;
	private Hashtable<Integer,Hashtable<Integer, MaxBox>> patchsEntree;
	public Hashtable<Integer,Hashtable<Integer,String>> patchsEntreeId;
	private Module parent;
	
	public ModuleEntree(MaxPatcher parentPatcher, String moduleEntreePatchBoxId, String moduleEntreeTabBoxId, String moduleEntreeSliderCorpsBoxId, Module parent){
		patchs = new Hashtable<Integer,Hashtable<Integer, MaxBox>>();
		patchsId = new Hashtable<Integer,Hashtable<Integer, String>>();
		patchsEntree = new Hashtable<Integer,Hashtable<Integer, MaxBox>>();
		patchsEntreeId = new Hashtable<Integer,Hashtable<Integer, String>>();
		this.inlets = parent.inlets;
		this.id = parent.getId();
		this.parentPatcher = parentPatcher;
		this.parent = parent;
		this.moduleEntreePatchBoxId = moduleEntreePatchBoxId;
		this.moduleEntreeTabBoxId = moduleEntreeTabBoxId;
		this.moduleEntreeSliderCorpsBoxId = moduleEntreeSliderCorpsBoxId;
		this.moduleEntreePatchBox = this.parentPatcher.getNamedBox(this.moduleEntreePatchBoxId);
		this.moduleEntreePatch = this.moduleEntreePatchBox.getSubPatcher();
		this.moduleEntreeTabBox = this.moduleEntreePatch.getNamedBox(this.moduleEntreeTabBoxId);
		this.moduleEntreeSliderCorpsBox = this.moduleEntreePatch.getNamedBox(this.moduleEntreeSliderCorpsBoxId);
		this.moduleEntreeSliderCorpsPatcher = this.moduleEntreeSliderCorpsBox.getSubPatcher();
		for(int i = 0 ; i <= inlets ; i++){
			patchs.put(i, new Hashtable<Integer, MaxBox>());
			patchsId.put(i, new Hashtable<Integer, String>());
			patchsEntree.put(i, new Hashtable<Integer, MaxBox>());
			patchsEntreeId.put(i, new Hashtable<Integer, String>());
		}
	}
	
	public ModuleEntree(MaxPatcher parent, String id, int inlets, int sizeY, String pattrId, Module javaParent){
		this.parent = javaParent;
		this.pattrId=pattrId;
		this.id = id;
		this.inlets = inlets;
		patchs = new Hashtable<Integer,Hashtable<Integer, MaxBox>>();
		patchsId = new Hashtable<Integer,Hashtable<Integer, String>>();
		patchsEntree = new Hashtable<Integer,Hashtable<Integer, MaxBox>>();
		patchsEntreeId = new Hashtable<Integer,Hashtable<Integer, String>>();
		for(int i = 0 ; i <= inlets ; i++){
			patchs.put(i, new Hashtable<Integer, MaxBox>());
			patchsId.put(i, new Hashtable<Integer, String>());
			patchsEntree.put(i, new Hashtable<Integer, MaxBox>());
			patchsEntreeId.put(i, new Hashtable<Integer, String>());
		}
		String[] pathEntree = {Tools.newPatch("moduleEntree"), "@presentation", "1", "@enablevscroll", "1"};
		moduleEntreePatchBoxId = Datas.getCgeId();
		moduleEntreePatchBox = parent.newDefault(0, 20, "bpatcher", Atom.newAtom(pathEntree));
		moduleEntreePatchBox.setName(moduleEntreePatchBoxId);
		moduleEntreePatch = moduleEntreePatchBox.getSubPatcher();
		parentPatcher = parent;
		Tools.move(moduleEntreePatchBox, 0, 20, Module.sliderEntreeSize, sizeY);
		
		// Dans le patch d'entrée
		String[] args = {Tools.newPatch("moduleEntreeTab"), "@args", String.valueOf(inlets), "@enablehscroll", "1", "@presentation_rect", "0", "0", "150", "27", "@presentation", "1"};
		moduleEntreeTabBox = moduleEntreePatch.newDefault(0, 0, "bpatcher", Atom.newAtom(args));
		moduleEntreeTabBoxId = Datas.getCgeId();
		moduleEntreeTabBox.setName(moduleEntreeTabBoxId);
		String[] args1 = {Tools.newPatch("moduleEntreeCorps"), "@presentation_rect", "0", "27", "150", String.valueOf(sizeY - 27), "@enablevscroll", "1", "@presentation", "1"};
		moduleEntreeSliderCorpsBoxId = Datas.getCgeId();
		moduleEntreeSliderCorpsBox = moduleEntreePatch.newDefault(0, 0, "bpatcher", Atom.newAtom(args1));
		moduleEntreeSliderCorpsBox.setName(moduleEntreeSliderCorpsBoxId);
		moduleEntreeSliderCorpsPatcher = moduleEntreeSliderCorpsBox.getSubPatcher();
		moduleEntreePatch.connect(moduleEntreeTabBox, 0, moduleEntreeSliderCorpsBox, 0);
		initSliders();
		setEntree(Datas.getEntrees());
	}

	public MaxPatcher getPatcher(){
		return moduleEntreePatch;
	}
	
	public MaxPatcher getPatcherInsideTab(){
		return moduleEntreeTabBox.getSubPatcher();
	}
	
	public MaxPatcher getPatcherInside(){
		return moduleEntreeSliderCorpsBox.getSubPatcher();
	}
	
	public void initSliders(){
		//Initialisation des sliders
		Hashtable<String, Module> modules = Datas.getModules();
		int i = 0;
		for(Module module : modules.values()){
			if(module.getOutlets() != 0){
				addSlider(module.getName(), module.getRang(), module.getId());
				
			}
			i++;
		}
	}
	
	public void setEntree(int nb){
		for(Hashtable<Integer, MaxBox> patch : patchsEntree.values()){
			for(MaxBox patch2 : patch.values()){
				patch2.remove();
			}
			patch.clear();
		}
		for(Hashtable<Integer, String> patch : patchsEntreeId.values()){
			patch.clear();
		}

		for(int i = 0 ; i <= inlets ; i++){
			for(int j =  0 ; j < Datas.getNbModulesEntrees() ; j++){
				String[] args = {"sendbox", patchs.get(i).get(j).getName(),"presentation_rect", String.valueOf(i * 150), String.valueOf((j+nb) * 20), "150", "20", "@presentation", "1"};
				moduleEntreeSliderCorpsPatcher.send("script", Atom.newAtom(args));
			}
		}
		for(int j = 0 ; j < nb ; j++){
			String pattrRand2 = UUID.randomUUID().toString();
			//String idPattr2 = pattrId + "::" + moduleEntreePatch.getName() + "::" + moduleEntreeSliderCorpsPatcher.getName()+ "::" + id + "-boiteInput0"+j + "::" + pattrRand2;
			String[] args = {Tools.newPatch("moduleEntreeSliderInputAll"), "@presentation", "1", "@presentation_rect", "0", String.valueOf(j * 20), "150", "20", "@varname", id + "-boiteInput0"+j, "@args", "Entrée" + (j+1), pattrRand2};
			patchsEntree.get(0).put(j, moduleEntreeSliderCorpsPatcher.newDefault(0, 0, "bpatcher", Atom.newAtom(args)));			
			patchsEntreeId.get(0).put(j, Datas.getCgeId());
			patchsEntree.get(0).get(j).setName(patchsEntreeId.get(0).get(j));
			//Tools.addToPattr(idPattr2);
			//Tools.midiLearnPatcher(patchsEntree.get(0).get(j).getSubPatcher());
			for(int i = 0 ; i < inlets ; i++){
				String pattrRand = UUID.randomUUID().toString();
				//String idPattr = pattrId + "::" + moduleEntreePatch.getName() + "::" + moduleEntreeSliderCorpsPatcher.getName()+ "::" + id + "-boiteInput" + (i+1) + j + "::" + pattrRand;
				String[] args1 = {Tools.newPatch("moduleEntreeSliderInput"),"@args", "Entrée" + (j+1) , id, String.valueOf(i), String.valueOf(j), pattrRand, "@presentation_rect", String.valueOf(150 * (i+1)), String.valueOf(j * 20), "150", "20", "@presentation", "1", "@varname", id + "-boiteInput" + (i+1) + j};
				//Tools.addToPattr(idPattr);
				
				patchsEntree.get(i+1).put(j, moduleEntreeSliderCorpsPatcher.newDefault(0, 0, "bpatcher", Atom.newAtom(args1)));
				patchsEntreeId.get(i+1).put(j, Datas.getCgeId());
				patchsEntree.get(i+1).get(j).setName(patchsEntreeId.get(i+1).get(j));
				//Tools.midiLearnPatcher(patchsEntree.get(i+1).get(j).getSubPatcher());
				moduleEntreeSliderCorpsPatcher.connect(patchsEntree.get(0).get(j), 0, patchsEntree.get(i+1).get(j), 0);
			}
		}
	}
	
	public void addSlider(String nom, int rang, String uniqueID){
		String type = "moduleEntreeSlider";
		if(this.parent.getRang() == rang){
			type = "moduleEntreeSliderOwn";
		}
		String pattrRand2 = UUID.randomUUID().toString();
		//String idPattr2 = pattrId + "::" + moduleEntreePatch.getName() + "::" + moduleEntreeSliderCorpsPatcher.getName()+ "::" + id +"-boite0"+rang + "::" + pattrRand2;
		String[] args = {Tools.newPatch(type + "All"), "@args", nom, pattrRand2, "@presentation_rect", "0", String.valueOf((rang + Datas.getEntrees()) * 20), "150", "20", "@presentation", "1", "@varname", id + "-boite0" + rang};
		//Tools.addToPattr(idPattr2);
		patchs.get(0).put(rang, moduleEntreeSliderCorpsPatcher.newDefault(0, 0, "bpatcher", Atom.newAtom(args)));
		patchsId.get(0).put(rang, Datas.getCgeId());
		patchs.get(0).get(rang).setName(patchsId.get(0).get(rang));
		//Tools.midiLearnPatcher(patchs.get(0).get(rang).getSubPatcher());
		for(int i = 0 ; i < inlets ; i++){
			String pattrRand = UUID.randomUUID().toString();
			//String idPattr = pattrId + "::" + moduleEntreePatch.getName() + "::" + moduleEntreeSliderCorpsPatcher.getName()+ "::" + id + "-boite" + (i+1) + rang + "::" + pattrRand;
			if(i!=1){
				String[] args1 = {Tools.newPatch(type),"@args", nom , id, String.valueOf(i), uniqueID, pattrRand, "@presentation_rect", String.valueOf(150 * (i+1)), String.valueOf((rang + Datas.getEntrees()) * 20), "150", "20", "@presentation", "1", "@varname", id + "-boite" + (i+1) + rang};
				patchs.get(i+1).put(rang, moduleEntreeSliderCorpsPatcher.newDefault(0, 0, "bpatcher", Atom.newAtom(args1)));
				patchsId.get(i+1).put(rang, Datas.getCgeId());
				patchs.get(i+1).get(rang).setName(patchsId.get(i+1).get(rang));
			}else{
				String[] args1 = {Tools.newPatch(type + "S"),"@args", nom , id, String.valueOf(i), uniqueID, pattrRand, "@presentation_rect", String.valueOf(150 * (i+1)), String.valueOf((rang + Datas.getEntrees()) * 20), "150", "20", "@presentation", "1", "@varname", id + "-boite" + (i+1) + rang};
				patchs.get(i+1).put(rang, moduleEntreeSliderCorpsPatcher.newDefault(0, 0, "bpatcher", Atom.newAtom(args1)));
				patchsId.get(i+1).put(rang, Datas.getCgeId());
				patchs.get(i+1).get(rang).setName(patchsId.get(i+1).get(rang));
			}
			//Tools.addToPattr(idPattr);
			//Tools.midiLearnPatcher(patchs.get(i+1).get(rang).getSubPatcher());
			moduleEntreeSliderCorpsPatcher.connect(patchs.get(0).get(rang), 0, patchs.get(i+1).get(rang), 0);
		}
	}
	
	public ArrayList<MaxBox> getPatches(){
		ArrayList<MaxBox> toReturn = new ArrayList<MaxBox>();
		for(Hashtable<Integer, MaxBox> entree : patchsEntree.values()){
			for(MaxBox entree2 : entree.values()){
				toReturn.add(entree2);
			}
		}
		for(Hashtable<Integer, MaxBox> entree : patchs.values()){
			for(MaxBox entree2 : entree.values()){
				toReturn.add(entree2);
			}
		}
		return toReturn;
	}
	
	public void supprSlider(int rang){
		for(int i = 0 ; i <= inlets ; i++){
			for(int j = (rang + 1) ; j < patchs.get(i).size(); j++){
				Hashtable<Integer, MaxBox> slider = patchs.get(i);
				Tools.move(slider.get(j), 150 * i, (((j - 1) + Datas.getEntrees()) * 20), 150, 20);
			}
		}
		for(int j = 0 ; j <= inlets ; j++){
			patchs.get(j).get(rang).remove();
		}
		for(int i = 0 ; i <= inlets ; i++){
			Hashtable<Integer, MaxBox> slider = patchs.get(i);
			Hashtable<Integer, String> sliderId = patchsId.get(i);
			for(int j = (rang + 1) ; j < patchs.get(i).size(); j++){
				slider.put(j-1, slider.get(j));
				sliderId.put(j-1, sliderId.get(j));
			}
			patchs.get(i).remove(slider.size() - 1);
			patchsId.get(i).remove(sliderId.size() - 1);
		}

		for(Hashtable<Integer, String> p : patchsId.values()){
			Enumeration<Integer> en = p.keys();
			while (en.hasMoreElements()) {
				Integer el = en.nextElement();
			}
		}
		
	}

	public void detectPatchs(String id, int nb){
		if(!id.equals("") && !id.equals(" ")){
			this.patchsId.get(nb).put(patchsId.get(nb).size(), id);
			this.patchs.get(nb).put(patchs.get(nb).size(), moduleEntreeSliderCorpsPatcher.getNamedBox(patchsId.get(nb).get(patchsId.get(nb).size()-1)));
		}
	}
	
	public void detectPatchsEntree(String id, int nb){
		if(!id.equals("") && !id.equals(" ")){
			this.patchsEntreeId.get(nb).put(patchsEntreeId.get(nb).size(), id);
			this.patchsEntree.get(nb).put(patchsEntree.get(nb).size(), moduleEntreeSliderCorpsPatcher.getNamedBox(patchsEntreeId.get(nb).get(patchsEntreeId.get(nb).size()-1)));
		}
	}
	
	public void moveSlider(int rang, int dest){
		for(int i = 0 ; i <= inlets ; i++){
				Hashtable<Integer, MaxBox> slider = patchs.get(i);
				Hashtable<Integer, String> sliderId = patchsId.get(i);
				Tools.move(slider.get(rang), 150 * i, ((dest + Datas.getEntrees()) * 20), 150, 20);
				Tools.move(slider.get(dest), 150 * i, ((rang + Datas.getEntrees()) * 20), 150, 20);
				MaxBox temp = slider.get(rang);
				String temp2 = sliderId.get(rang);
				slider.put(rang, slider.get(dest));
				sliderId.put(rang, sliderId.get(dest));
				slider.put(dest, temp);
				sliderId.put(dest, temp2);
		}
	}
	
}
