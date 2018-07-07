package module;

import tools.Datas;
import tools.Tools;

import java.util.UUID;

import com.cycling74.max.*;

public class Module {
	
	private String id; // id du module (ref hashTable modules)
	private MaxPatcher patchCorps;
	public String patchCorpsId;
	private MaxBox patchCorpsBox;
	private ModulePatch modulePatch;
	private ModuleEntree moduleEntree;
	//private ModuleEnvoi moduleEnvoi;
	private ModuleSortie moduleSortie;
	public String moduleTableMixagePatcherBoxId;
	private MaxPatcher moduleTableMixagePatcher;
	private MaxBox moduleTableMixagePatcherBox;
	public int inlets;
	public int outlets;
	public int sizeX;
	public int sizeY;
	public int rang;
	public String nom;
	public static int sliderEntreeSize = 150;
	public static int sortieSize = 100;
	
	public Module(String id, String patchCorpsId, String moduleTableMixagePatcherBoxId, int inlets, int outlets, int sizeX, int sizeY, int rang, String nom, String moduleEntreePatchBoxId, String moduleEntreeTabBoxId, String moduleEntreeSliderCorpsBoxId, String moduleSortiePatchBoxId, String modulePatchId, String toModuleId){
		this.id = id;
		this.patchCorpsId = patchCorpsId;
		this.moduleTableMixagePatcherBoxId = moduleTableMixagePatcherBoxId;
		this.inlets = inlets;
		this.outlets = outlets; 
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.rang = rang;
		this.nom = nom;
		this.patchCorpsBox = Datas.getMainPatcher().getNamedBox(patchCorpsId);
		this.patchCorps = this.patchCorpsBox.getSubPatcher();
		if(this.outlets > 0){
			this.moduleTableMixagePatcherBox = Datas.getTableMixagePatcher().getNamedBox(moduleTableMixagePatcherBoxId);
			this.moduleTableMixagePatcher = this.moduleTableMixagePatcherBox.getSubPatcher();
		}else{
			this.moduleTableMixagePatcherBox = Datas.getMainPatcher().getNamedBox(moduleTableMixagePatcherBoxId);
			this.moduleTableMixagePatcher = this.moduleTableMixagePatcherBox.getSubPatcher();
		}
		this.modulePatch = new ModulePatch(patchCorps, modulePatchId, toModuleId, this);
		if(this.inlets > 0){
			this.moduleEntree = new ModuleEntree(patchCorps, moduleEntreePatchBoxId, moduleEntreeTabBoxId, moduleEntreeSliderCorpsBoxId, this);
		}else{
			this.moduleEntree = null;
		}
		if(this.outlets > 0){
			this.moduleSortie = new ModuleSortie(patchCorps, moduleSortiePatchBoxId, this);
		}else{
			this.moduleSortie = null;
		}
	}
	
	public Module(String path, String id, String nom){
		this.rang = Datas.getNbModulesEntrees();
		this.nom = nom;
		this.id = id;
		String pathCorps = Tools.newPatch("moduleCorps");
		String[] args1 = {pathCorps.split("/")[pathCorps.split("/").length-1], "1", "@args", id};
		patchCorpsBox = Datas.getMainPatcher().newDefault(0, 30 * Datas.getNbModules(), "poly~", Atom.newAtom(args1));
		String[] argName = {pathCorps};
		patchCorpsId=Datas.getCgeId();
		patchCorpsBox.send("patchername", Atom.newAtom(argName));
		patchCorpsBox.setName(this.patchCorpsId);
		String[] args2 = {id + "open"};
		String pattrId = nom + "-" + id + ".1";
		MaxBox receive = Datas.getMainPatcher().newDefault(1000, 30 * Datas.getNbModules(), "receive", Atom.newAtom(args2));
		Datas.getMainPatcher().connect(receive, 0, patchCorpsBox, 0);
		patchCorps = patchCorpsBox.getSubPatcher();
		String[] args11 = {nom + "-" + id};
		patchCorps.send("title", Atom.newAtom(args11));
		String[] args3 = {"1"};
		patchCorps.send("presentation", Atom.newAtom(args3));
		patchCorps.getWindow().setFloat(true);
		modulePatch = new ModulePatch(patchCorps, id, path);
		inlets = modulePatch.getInlets();
		outlets = modulePatch.getOutlets();
		sizeX = modulePatch.getSizeX();
		sizeY = modulePatch.getSizeY();
		if(inlets > 0){
			//Si il y a des entrées, création du module d'entrée
			moduleEntree = new ModuleEntree(patchCorps, id, inlets, sizeY, pattrId, this);
		}else{
			//Si il n'y a pas d'entrée, pas de module d'entrée et on décale le patch contre le bord
			moduleEntree = null;
			Tools.move(modulePatch.getModulePatchBox(), 0, 20, sizeX, sizeY);
		}
		//moduleEnvoi = new ModuleEnvoi(patchCorps, id);
		if(outlets != 0){
			if(moduleEntree != null){
				moduleSortie = new ModuleSortie(patchCorps, id, sizeX + sliderEntreeSize, sizeY, outlets);
			}else{
				moduleSortie = new ModuleSortie(patchCorps, id, sizeX, sizeY, outlets);
			}
			patchCorps.connect(moduleSortie.getParentBox(), 1, modulePatch.toModule, 2);
		}else{
			moduleSortie = null;
			String[] args4 = {id + "mute"};
			MaxBox receive1 = patchCorps.newDefault(0, 0, "receive", Atom.newAtom(args4));
			patchCorps.connect(receive1, 0, patchCorps.getNamedBox("mute"), 0);
		}
		patchCorps.getWindow().setLocation(0, 0, sliderEntreeSize + sizeX + sortieSize, sizeY + 20);	
		if(outlets != 0){
			String pathModule = Tools.newPatch("tableMixageModule");
			String[] args = {pathModule, "@args", id, String.valueOf(Datas.getSorties()), this.nom, UUID.randomUUID().toString(), "@presentation", "1"};
			moduleTableMixagePatcherBox = Datas.getTableMixagePatcher().newDefault(0, 0, "bpatcher", Atom.newAtom(args));
			moduleTableMixagePatcherBoxId = Datas.getCgeId();
			moduleTableMixagePatcherBox.setName(moduleTableMixagePatcherBoxId);
			moduleTableMixagePatcher = moduleTableMixagePatcherBox.getSubPatcher();
			Tools.move(moduleTableMixagePatcherBox, Datas.getScreenX() - (200 + 100 * (1 + Datas.getNbModulesEntrees())), 0, 100, 150);
			Datas.setNbModulesWithOut(Datas.getNbModulesEntrees() + 1);
			// Connect
			patchCorps.connect(moduleSortie.getParentBox(), 0, patchCorps.getNamedBox("mute"), 0);
		}else{
			String pathModule = Tools.newPatch("tableMixageModuleWithoutOut");
			String[] args = {pathModule, "@args", id, nom, UUID.randomUUID().toString(), "@presentation", "1"};
			moduleTableMixagePatcherBox = Datas.getMainPatcher().newDefault(0, 0, "bpatcher", Atom.newAtom(args));
			moduleTableMixagePatcherBoxId = Datas.getCgeId();
			moduleTableMixagePatcherBox.setName(moduleTableMixagePatcherBoxId);
			moduleTableMixagePatcher = moduleTableMixagePatcherBox.getSubPatcher();
			Tools.move(moduleTableMixagePatcherBox, Datas.getScreenX() - (200 + 100 * (Datas.getNbModules() - Datas.getNbModulesEntrees())), Datas.getScreenY() - 222, 105, 24);
		}
		if(inlets > 0 && outlets > 0){
			patchCorps.getWindow().setLocation(0, 0, sliderEntreeSize + sizeX + sortieSize, sizeY + 20);
		}else if (inlets == 0 && outlets > 0){
			patchCorps.getWindow().setLocation(0, 0, sizeX + sortieSize, sizeY + 20);
		}else if (inlets > 0 && outlets == 0){
			patchCorps.getWindow().setLocation(0, 0, sliderEntreeSize + sizeX, sizeY + 20);
		}else{
			patchCorps.getWindow().setLocation(0, 0, sizeX, sizeY + 20);
		}
		
		if(outlets == 0){
			this.rang = Datas.getNbModules() - Datas.getNbModulesEntrees();
		}
		
	}
	
	public int getRang() {
		return this.rang;
	}
	
	public void setRang(int rang){
		this.rang = rang;
	}
	
	public int getInlets() {
		return inlets;
	}
	
	public int setEntrees(int nb) {
		this.moduleEntree.setEntree(nb);
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

	public void setId(String id) {
		this.id = id;
	}

	public void addSlider(String name, String uniqueID){
		if(inlets != 0){
			moduleEntree.addSlider(name, Datas.getNbModulesEntrees() - 1, uniqueID);
		}
	}
	
	public String getName(){
		return this.nom;
	}
	
	public String getId(){
		return id;
	}

	public MaxPatcher getPatchSortie() {
		return moduleSortie.getPatcher();
	}
	
	public ModulePatch getModulePatch() {
		return modulePatch;
	}
	
	public MaxPatcher getPatchEntree() {
		return moduleEntree.getPatcher();
	}
	
	public ModuleEntree getModuleEntree() {
		return moduleEntree;
	}
	
	public MaxPatcher getModuleTableMixagePatcher(){
		return moduleTableMixagePatcher;
	}
	
	public MaxPatcher getPatchCorps() {
		return patchCorps;
	}

	public MaxBox getPatchCorpsBox() {
		return patchCorpsBox;
	}
	
	public ModuleSortie getModuleSortie(){
		return moduleSortie;
	}
	
	public void supprModule(int rang){
		if(this.rang > rang && outlets>0){
			this.rang--;
			Tools.move(moduleTableMixagePatcherBox, Datas.getScreenX() - (200 + 100 * (1 + this.rang)), 0, 100, 150);
		}
		if(moduleEntree != null){
			moduleEntree.supprSlider(rang);
		}
	}
	
	public void moveModuleEntree(int rang, int dest){
		if(this.moduleEntree != null){
			moduleEntree.moveSlider(rang, dest);
		}
	}
	
	public void moveModule(int rang){
		if(outlets!=0){
			Tools.move(moduleTableMixagePatcherBox, Datas.getScreenX() - (200 + 100 * (1 + rang)), 0, 100, 150);
		}else{
			Tools.move(moduleTableMixagePatcherBox, Datas.getScreenX() - (200 + 100 * rang), Datas.getScreenY() - 222, 105, 24);
		}
		this.rang = rang;
	}
	
	public void moveModuleWithoutOutput(int rang){
		if(this.outlets == 0 && this.rang > rang){
			this.rang--;
			Tools.move(moduleTableMixagePatcherBox, Datas.getScreenX() - (200 + 100 * this.rang), Datas.getScreenY() - 222, 105, 24);
		}
	}
	
	public void delete(){
		patchCorpsBox.remove();
		moduleTableMixagePatcherBox.remove();
	}
	
}
