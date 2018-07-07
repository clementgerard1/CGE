package tools;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.UUID;

import com.cycling74.max.MaxBox;
import com.cycling74.max.MaxObject;
import com.cycling74.max.MaxPatcher;

import module.Module;

public class Datas implements Serializable{
	
	private static Datas instance = null;
	private static MaxPatcher mainPatcher;
	private static String mainFilePath;
	private static String mainFolderPath;
	public static Hashtable<String, Module> modules = new Hashtable<String, Module>();
	private static int nbModules = 0;
	private static int nbModulesWithOut = 0;
	private static MaxBox TableMixagePatcherBox;
	private static MaxBox configBox;
	private static MaxPatcher configPatcher;
	private static MaxPatcher TableMixagePatcher;
	private static MaxPatcher masterModule;
	private static MaxPatcher masterModulePatch;
	private static MaxBox EntreePatcherBox;
	private static MaxPatcher EntreePatcher;
	private static MaxBox masterSliderPatcherBox;
	private static MaxPatcher masterSliderPatcher;
	private static int screenX;
	private static int screenY;
	private static int sorties;
	private static int entrees;
	private static String uniqueId = "-1";
	public static ArrayList<String> rangToId = new ArrayList<String>();
	public static int cgeId = 0;
	public static MaxObject him;
	public static Boolean first;
	
	//Pour la sauvegarde de la configuration
	public static File configFile;
	public static String tableMixagePatcherBoxId;
	public static String configBoxId;
	public static String entreeExtPatcherBoxId;
	
	
	public static String getUniqueId(){
		if(uniqueId.equals("-1")){
			uniqueId = UUID.randomUUID().toString();
		}
		return uniqueId;
	}
	
	public static String getCgeId(){
		Datas.cgeId++;
		return "cgeId_"+Datas.cgeId;
	}
	
	public static int getSorties() {
		return sorties;
	}

	public static void setSorties(int sorties) {
		Datas.sorties = sorties;
	}

	public static int getEntrees() {
		return Datas.entrees;
	}

	public static void setEntrees(int entrees) {
		Datas.entrees = entrees;
		for(Module module : modules.values()){
			if(module.getInlets() != 0){
				module.setEntrees(entrees);
			}
		}
	}

	public static int getScreenX() {
		return screenX;
	}

	public static void setScreenX(int screenX) {
		Datas.screenX = screenX;
	}

	public static int getScreenY() {
		return screenY;
	}

	public static void setScreenY(int screenY) {
		Datas.screenY = screenY;
	}

	public static void ajouterModule(String path){
		String uniqueID = UUID.randomUUID().toString();
		String[] splits = path.split("/");
		String nom = splits[splits.length - 1].split(".maxpat")[0];
		modules.put(uniqueID, new Module(path, uniqueID, nom));
		int nbOutlets = modules.get(uniqueID).getOutlets();
		nbModules = modules.size();
		//On ajoute à la table d'ID et rang
		rangToId.add(uniqueID);
		if(nbOutlets != 0){			
			//On actualise les tables d'entrée
			for(Module module : modules.values()){
				module.addSlider(nom, uniqueID);
			}
		}
	}
	
	public static void move(int rang, int dest){
		if(Datas.modules.size()>0){
			//Détection pour savoir si les deux modules sont du même type
			String id = rangToId.get(rang);
			String idRelatif = rangToId.get(dest);
			Module module = modules.get(id);
			Module moduleRelatif = modules.get(idRelatif);
			int rangId = module.getRang();
			int rangIdRelatif = moduleRelatif.getRang();
			if(!((module.getOutlets()>0 && moduleRelatif.getOutlets()>0) || (module.getOutlets()==0 && moduleRelatif.getOutlets()==0))){
				return;
			}
			if(module.getOutlets() != 0){			
				//On actualise les tables d'entrée
				for(Module mod : modules.values()){
					mod.moveModuleEntree(rangId, rangIdRelatif);
				}
			}
			module.moveModule(rangIdRelatif);
			moduleRelatif.moveModule(rangId);
			
			//Actualisation
			module.setRang(rangIdRelatif);
			moduleRelatif.setRang(rangId);
			String temp = rangToId.get(dest);
			rangToId.set(dest, rangToId.get(rang));
			rangToId.set(rang, temp);
		}
		
	}
	
	public static void supprimerModule(int rang){
		if(Datas.modules.size()>0){
			String id = rangToId.get(rang);
			Module module = modules.get(id);
			if(module.getOutlets() != 0){			
				//On actualise les tables d'entrée
				for(Module mod : modules.values()){
					mod.supprModule(module.getRang());
				}
			}else{
				for(Module mod : modules.values()){
					mod.moveModuleWithoutOutput(module.getRang());
				}
			}
			module.delete();
			modules.remove(id);
			rangToId.remove(rang);
			nbModules--;
		}
	}

	public static void setMasterModule(MaxPatcher p){
		masterModule = p;
		masterModulePatch = masterModule.getNamedBox("CGEMasterModulePatch").getSubPatcher();
	}
	
	public static MaxPatcher getMasterModule(){
		return masterModule;
	}
	
	public static MaxPatcher getMasterModulePatch(){
		return masterModulePatch;
	}
	
	public static MaxPatcher getMainPatcher() {
		return mainPatcher;
	}
	public static void setMainPatcher(MaxPatcher mainPatcher) {
		Datas.mainPatcher = mainPatcher;
	}
	
	public static MaxPatcher getConfigPatcher() {
		return Datas.configPatcher;
	}
	
	public static String getMainFilePath() {
		return mainFilePath;
	}
	public static void setMainFilePath(String mainFilePath) {
		Datas.mainFilePath = mainFilePath;
	}
	public static String getMainFolderPath() {
		return mainFolderPath;
	}
	public static void setMainFolderPath(String mainFolderPath) {
		Datas.mainFolderPath = mainFolderPath;
	}

	public static Hashtable<String, Module> getModules() {
		return modules;
	}
	
	public static int getNbModulesEntrees() {
		int count = 0;
		for(Module module : Datas.getModules().values()){
			if(module.getOutlets() != 0){
				count++;
			}
		}
		return count;
	}

	public static int getNbModules() {
		return nbModules;
	}
	
	public static int getNbModulesWithOut() {
		return nbModulesWithOut;
	}
	
	public static void setNbModulesWithOut(int nbModules) {
		Datas.nbModulesWithOut = nbModules;
	}
	
	public static void setNbModules(int nbModules) {
		Datas.nbModules = nbModules;
	}

	public static void setTableMixagePatcherBox(MaxBox box) {
		TableMixagePatcher = box.getSubPatcher();
		TableMixagePatcherBox = box;
		masterSliderPatcherBox = TableMixagePatcher.getNamedBox("cgeMasterSlider");
		masterSliderPatcher = masterSliderPatcherBox.getSubPatcher();
	}
	
	public static void setEntreeExtPatcherBox(MaxBox box) {
		EntreePatcher = box.getSubPatcher();
		EntreePatcherBox = box;
	}
	
	public static MaxBox getEntreeExtPatcherBox(){
		return EntreePatcherBox;
	}
	
	public static MaxPatcher getEntreeExtPatcher(){
		return EntreePatcher;
	}
	
	public static MaxPatcher getTableMixagePatcher() {
		return TableMixagePatcher;
	}

	public static void setTableMixagePatcher(MaxPatcher tableMixagePatcher) {
		TableMixagePatcher = tableMixagePatcher;
	}
	
	public static void setConfigBox(MaxBox configBox) {
		Datas.configBox = configBox;
		Datas.configPatcher = configBox.getSubPatcher();
	}
	
	public static void setConfigPatcher(MaxPatcher configPatcher) {
		Datas.configPatcher = configPatcher;
	}
	
	public static MaxBox getConfigBox(){
		return configBox;
	}

	public static MaxBox getMasterSliderPatcherBox() {
		return masterSliderPatcherBox;
	}

	public static void setMasterSliderPatcherBox(MaxBox masterSliderPatcherBox) {
		Datas.masterSliderPatcherBox = masterSliderPatcherBox;
	}

	public static MaxPatcher getMasterSliderPatcher() {
		return masterSliderPatcher;
	}

	public static void setMasterSliderPatcher(MaxPatcher masterSliderPatcher) {
		Datas.masterSliderPatcher = masterSliderPatcher;
	}

	public static MaxBox getTableMixagePatcherBox() {
		return TableMixagePatcherBox;
	}
	
}
