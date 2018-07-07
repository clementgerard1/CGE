import java.io.File;
import java.io.Serializable;

import com.cycling74.max.*;

import tools.*;

public class CGE extends MaxObject{
	
	
	public CGE(){
		Datas.modules.clear();
		Datas.rangToId.clear();
		Datas.him = this;
		Datas.setMainPatcher(this.getParentPatcher());
		Datas.setMainFilePath(this.getParentPatcher().getFilePath());
		Datas.setMainFolderPath(this.getParentPatcher().getPath());
		//Détermine si le patch existe déjà
		Datas.first = true;
		File cgeConfig = new File(this.getParentPatcher().getPath() + "/CGEConfiguration.config"); 
		if(cgeConfig.exists()){
			Datas.first = false;		
		}else{
			Datas.first = true;
		}
		if(Datas.first){
			// Reunion des informations dans la classe Datas
			Datas.cgeId = 0;
			Datas.tableMixagePatcherBoxId = "";
			Datas.configBoxId = "";
			Datas.entreeExtPatcherBoxId = "";
			Datas.getModules().clear();
			Datas.setNbModules(0);
			Datas.setEntrees(0);
			Datas.setSorties(2);
			Datas.setNbModulesWithOut(0);
			//Création table de mixage et du module de master
			String masterModule = Tools.newPatch("master");
			masterModule = masterModule.split("/")[masterModule.split("/").length-1];
			String masterModuleEdit = Tools.newPatch("masterPatch");
			Tools.newFile("presetFile");
			masterModuleEdit = masterModuleEdit.split("/")[masterModuleEdit.split("/").length-1];
			String[] args = {Tools.newPatch("tableMixage"), "@args", String.valueOf(Datas.getSorties()), masterModuleEdit, masterModule, "@presentation", "1", "@enablehscroll", "1"};
			Datas.setTableMixagePatcherBox(this.getParentPatcher().newDefault(0, 0, "bpatcher", Atom.newAtom(args)));
			Datas.tableMixagePatcherBoxId = Datas.getCgeId();
			Datas.getTableMixagePatcherBox().setName(Datas.tableMixagePatcherBoxId);
			Datas.setMasterModule(Datas.getTableMixagePatcher().getNamedBox("cgeMasterSlider").getSubPatcher());
			//String[] argName = {masterModule};
			//Datas.getMasterModule().send("patchername", Atom.newAtom(argName));
			
			
			//Création du patch de config
			String pathConfig = Tools.newPatch("config");
			String[] args3 = {pathConfig.split("/")[pathConfig.split("/").length-1], "1", "@presentation", "1"};
			Datas.setConfigBox(this.getParentPatcher().newDefault(0, 0, "poly~",  Atom.newAtom(args3)));
			Datas.configBoxId = Datas.getCgeId();
			Datas.getConfigBox().setName(Datas.configBoxId);
			this.getParentPatcher().connect(this.getParentPatcher().getNamedBox("pcontrol"), 0, Datas.getConfigBox(), 0);
			String[] argName = {pathConfig};
			Datas.getConfigBox().send("patchername", Atom.newAtom(argName));
			Datas.setConfigPatcher(Datas.getConfigBox().getSubPatcher());
			
			//Création du patch de gestion des entrées sons
			Datas.entreeExtPatcherBoxId = Datas.getCgeId();
			String[] args2 = {Tools.newPatch("entreeSons"), String.valueOf(Datas.getEntrees()), "@presentation", "1", "@presentation_rect", "200", "0", "2000", "150"};
			Datas.setEntreeExtPatcherBox(Datas.getConfigPatcher().newDefault(30, 0, "bpatcher" , Atom.newAtom(args2)));
			Datas.getEntreeExtPatcherBox().setName(Datas.entreeExtPatcherBoxId);
			
			Tools.saveConfig(this.getParentPatcher().getPath() + "/ressources/copy/CGEConfiguration.config");
			
		}
	}
	
	public void init(){
		if(!Datas.first){
			Tools.readConfig();
		}
	}

	public void screensize(Atom[] args){
		Datas.setScreenX(args[2].getInt());
		Datas.setScreenY(args[3].getInt());
		this.getParentPatcher().getWindow().setLocation(0, 44, args[2].getInt(), args[3].getInt());
		Tools.move(Datas.getTableMixagePatcherBox(), 100, args[3].getInt() - 197, args[2].getInt() - 100, 150);
		Tools.move(Datas.getMasterSliderPatcherBox(), args[2].getInt() - 200, 0, 100, 150);
		Datas.setSorties(Datas.getSorties());
		Datas.setEntrees(Datas.getEntrees());
		Datas.him.outlet(1, Datas.getSorties());
	}

	public void setSortie(int i){
		Datas.setSorties(i);
	}
	
	public void setEntree(int i){
		Datas.setEntrees(i);
	}
	
	public void export(String s){
		Tools.exportPatchs();
	}
	
	public void export2(String s){
		Tools.export2Patchs(s);
	}
	
	public void test(){
		
	}
	
	public void move(int rang, int dest){
		Datas.move(rang, dest);
	}
	
	public void delete(int rang){
		Datas.supprimerModule(rang);
	}
	
	public void deleteAll(){
		Tools.deleteAllPatches();
	}
	
	public void ajouter(final String path){
		Datas.ajouterModule(path);
	}
	
}