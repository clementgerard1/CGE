package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.cycling74.max.*;

import module.Module;

public abstract class Tools{
	
	static int id = 0;
	
	public static void deleteAllPatches(){
		File mainFold = new File(Datas.getMainFolderPath() + "/ressources/copy");
		deleteAllProcess(mainFold);
		Datas.setNbModules(0);
		id = 0;
		for(Module module : Datas.getModules().values()){
			module.getPatchCorpsBox().remove();
		}
		Datas.getModules().clear();
		
	}
	
	private static void deleteAllProcess(File file){
		if(file.isDirectory()){
			for(File file2 : file.listFiles()){
				deleteAllProcess(file2);
			}
		}else{
			file.delete();
		}
	}
		
	public static String newPatch(String nom){
		return newPatch(nom, null);
	}
	
	public static String formatPath(String path){
		String pathFormate = path.replace("Macintosh HD:", "");
		return pathFormate;
	}
	
	public static void move(MaxBox box, int x1, int y1, int  x2, int y2){
		String[] args = {String.valueOf(x1), String.valueOf(y1) , String.valueOf(x2), String.valueOf(y2)};
		if(box.getSubPatcher() != null){
			String ancien = box.getName();
			box.setName("cgeForRect");//+UUID.randomUUID().toString());
			String[] args1 = { "sendbox", "cgeForRect", "presentation_rect", String.valueOf(x1),  String.valueOf(y1), String.valueOf(x2), String.valueOf(y2)};
			box.getPatcher().send("script", Atom.newAtom(args1));
			box.setName(ancien);
		}else{
			box.send("presentation_rect", Atom.newAtom(args));
		}
		
	}
	
	public static void exportPatchs(){
		
		//Sauvegarde des patchs 
		Tools.save(Datas.getTableMixagePatcher());
		Tools.save(Datas.getMasterSliderPatcher());
		Tools.save(Datas.getEntreeExtPatcher());
		Tools.save(Datas.getMasterModule());
		Tools.save(Datas.getMasterModulePatch());
		Hashtable<String, Module> modules = Datas.getModules();
		for(Module module : modules.values()){
			Tools.save(module.getModuleTableMixagePatcher());
			Tools.save(module.getPatchCorps());
			if(module.getInlets() != 0){
				Tools.save(module.getPatchEntree());
				Tools.save(module.getModuleEntree().getPatcherInside());
				Tools.save(module.getModuleEntree().getPatcherInsideTab());
			}
			if(module.getOutlets() != 0){
				Tools.save(module.getPatchSortie());
			}
			Tools.save(module.getModulePatch().getModulePatch());
			//for(MaxBox box : module.getModuleEntree().getPatches()){
			//	Tools.save(box.getSubPatcher());
			//}
		}	
		
	}
	
	public static void export2Patchs(String path){
		Tools.saveConfig(path+"/CGEConfiguration.config");
		File mainFold = new File(formatPath(path));
		// Création du dossier
		mainFold.mkdir();
		// Copie des fichier
		//Export CGE
		if(Datas.first){
			File parentFile = new File(Datas.getMainFolderPath() + "/CGE.maxpat");
			File newFile = new File(mainFold + "/#" + mainFold.getName() + ".maxpat");
			String[] args1 = { "#" + mainFold.getName() };
			Datas.getMainPatcher().send("title", Atom.newAtom(args1));
			parentFile.renameTo(newFile);
			exportPatch(newFile.getAbsolutePath(), parentFile.getAbsolutePath());
			//Export JS
			File ressources = new File(formatPath(path+"/ressources"));
			ressources.mkdir();
			File ressources2 = new File(formatPath(path+"/ressources/copy"));
			ressources2.mkdir();
			File ressources6 = new File(formatPath(path+"/ressources/copy/global"));
			ressources6.mkdir();
			File ressources7 = new File(formatPath(path+"/ressources/copy/module"));
			ressources7.mkdir();
			File ressources8 = new File(formatPath(path+"/ressources/copy/patchesLoaded"));
			ressources8.mkdir();
			File ressources9 = new File(formatPath(path+"/ressources/copy/tableMixage"));
			ressources9.mkdir();
			
			File ressources3 = new File(formatPath(path+"/ressources/js"));
			ressources3.mkdir();
			File ressources4 = new File(formatPath(path+"/ressources/modele"));
			ressources4.mkdir();
			File ressources10 = new File(formatPath(path+"/ressources/modele/global"));
			ressources10.mkdir();
			File ressources11 = new File(formatPath(path+"/ressources/modele/module"));
			ressources11.mkdir();
			File ressources13 = new File(formatPath(path+"/ressources/modele/tableMixage"));
			ressources13.mkdir();
			
			File ressources5 = new File(formatPath(path+"/ressources/tools"));
			ressources5.mkdir();
			
			//Export cgeJava
			File bin = new File(formatPath(path+"/bin"));
			bin.mkdir();
			File bin1 = new File(formatPath(path+"/bin/module"));
			bin1.mkdir();
			File bin2 = new File(formatPath(path+"/bin/tools"));
			bin2.mkdir();
			
			File mainRessources = new File(Datas.getMainFolderPath() + "/ressources");
			moveAllFolder(mainRessources, ressources, "", true);
			
			File mainBin = new File(Datas.getMainFolderPath() + "/bin");
			moveAllFolder(mainBin, bin, "", true);
		}
		
		//Export patchGlobaux
		File mainFoldPatcher = new File(Datas.getMainFolderPath() + "/ressources/copy");
		moveAllPatches(mainFoldPatcher, mainFold);
		
	}
	
	private static void save(MaxPatcher patch){
		String[] arg = {};
		patch.send("front", Atom.newAtom(arg));
		patch.send("dirty", Atom.newAtom(arg));
		//patch.send("write", Atom.newAtom(arg));
		//patch.send("wclose", Atom.newAtom(arg));
	}
	
	private static void moveAllFolder(File file, File mainFold, String entre ,Boolean t){
		//Export Patch
		if(file.isDirectory()){
			for(File file2 : file.listFiles()){
				if(!t){
					moveAllFolder(file2, mainFold, entre + file.getName() +"/", false);
				}else{
					moveAllFolder(file2, mainFold, entre +"/", false);
				}
			}
		}else{
			exportPatch(file.getPath(), mainFold.getPath() + "/" + entre + file.getName());
		}
	}
	
	private static void moveAllPatches(File file, File mainFold){
		//Export Patch
		if(file.isDirectory()){
			for(File file2 : file.listFiles()){
				moveAllPatches(file2, mainFold);
			}
		}else{
			exportPatch(file.getPath(), mainFold.getPath() + "/" + file.getName());
		}
	}
	
	public static void exportPatch(String pathRef, String pathDest){
		FileInputStream filesource = null;
        FileOutputStream fileDestination = null;
        try{
            filesource = new FileInputStream(pathRef);
            fileDestination = new FileOutputStream(pathDest);
            byte buffer[] = new byte[512 * 1024];
            int nblecture;
            while((nblecture = filesource.read(buffer)) != -1){
                fileDestination.write(buffer, 0, nblecture);
            }
            filesource.close();
            fileDestination.close();
        }catch(FileNotFoundException nf){
            nf.printStackTrace();
        }catch(IOException io){
            io.printStackTrace();
        }finally{
            try{
                filesource.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                fileDestination.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        } 
	}
	
	public static void midiLearnPatcher(MaxPatcher patcher){
		for(MaxBox box : patcher.getAllBoxes()){
			midiLearn(box);
		}
	}
	
	public static void midiLearn(MaxBox box){
		String nom = null;
		MaxPatcher patch = box.getPatcher();
		if(box.getName().length() == 0){
			nom = UUID.randomUUID().toString();
			box.setName(nom);
		}else{
			nom = box.getName();
		}
		int x = box.getRect()[0];
		int y = box.getRect()[0];
		MaxBox js = null;
		if(box.getMaxClass().equals("toggle") || box.getMaxClass().equals("button")){
			String[] args = {"midi.js", nom, "note", nom + "cgeDetect"};
			js = patch.newDefault(x, y, "js", Atom.newAtom(args));
			js.setName(nom + "cgeDetect");	
			js.setHidden(true);
			js.send("init", null);
		}else if(box.getMaxClass().equals("int") || box.getMaxClass().equals("float") || box.getMaxClass().equals("slider") || box.getMaxClass().equals("multislider") || box.getMaxClass().equals("dial")){
			String[] args = {"midi.js", nom, "ctl", nom + "cgeDetect", "@varname", nom + "cgeDetect"};
			js = patch.newDefault(x, y, "js", Atom.newAtom(args));
			js.setName(nom + "cgeDetect");
			js.setHidden(true);
			js.send("init", null);
		}
	}
	
	
	public static int inlets = 0; //Systeme qui attend le résultat du javascript
	public static int outlets = 0;
	public static Hashtable<String, Object> determinInfos(MaxPatcher patch){
		Tools.inlets = 0;
		Tools.outlets = 0;
		int[] size;
		Hashtable<String, Object> infos = new Hashtable<String, Object>();
		for(MaxBox box : patch.getAllBoxes()){
			if(box.getMaxClass().equals("inlet")){
				inlets++;
			}else if(box.getMaxClass().equals("outlet")){
				outlets++;
			}
		}
		size = patch.getWindow().getSize();
		if(size[1] < 150){
			size[1] = 150;
		}
		infos.put("inlets", inlets);
		infos.put("outlets", outlets);
		infos.put("sizeX", size[0]);
		infos.put("sizeY", size[1]);
		return infos;		
	}
	
	public static void addToPattr(String id){
		MaxBox stor = Datas.getMainPatcher().getNamedBox("pattrStorage");
		String[] args = {id};
		stor.send("subscribe", Atom.newAtom(args));
	}
	
	public static boolean newFile(String nom){
		return newFile(nom, null);
	}
	
	public static boolean newFile(String nom, String pathModule){
		
		String path = null;
		if(nom.equals("presetFile")){
			path="preset.txt";
		}
		boolean destination = false;
		try {
			destination = new File(Datas.getMainFolderPath() + "/ressources/copy/" + path).createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return destination;
        
	}
	
	public static String newPatch(String nom, String pathModule){
		
		String reference = null, destination = null, path = null;
		
		//Determination du path en fonction du nom
		if(nom.equals("moduleEntree")){
			path = "module/entree";
		}else if(nom.equals("moduleCorps")){
			path = "module/corps";
		}else if(nom.equals("moduleEnvoi")){
			path = "module/envoi";
		}else if(nom.equals("moduleSortie")){
			path = "module/sortieStandart";
		}else if(nom.equals("tableMixage")){
			path = "tableMixage/corps";
		}else if(nom.equals("tableMixageMaster")){
			path = "tableMixage/master";
		}else if(nom.equals("tableMixageModule")){
			path = "tableMixage/module";
		}else if(nom.equals("tableMixageModuleWithoutOut")){
			path = "tableMixage/moduleWithoutOut";
		}else if(nom.equals("moduleSortieMoteur")){
			path = "module/sortieStandartMoteur";
		}else if(nom.equals("masterSlider")){
			path = "tableMixage/tableSlider";
		}else if(nom.equals("moduleEntreeTab")){
			path = "module/entreeTab";
		}else if(nom.equals("moduleEntreeCorps")){
			path = "module/sliderCorps";
		}else if(nom.equals("moduleEntreeSliderAll")){
			path = "module/sliderAll";
		}else if(nom.equals("moduleEntreeSlider")){
			path = "module/slider";
		}else if(nom.equals("moduleEntreeSliderOwnAll")){
			path = "module/sliderOwnAll";
		}else if(nom.equals("moduleEntreeSliderOwn")){
			path = "module/sliderOwn";
		}else if(nom.equals("moduleEntreeSliderInputAll")){
			path = "module/sliderAllInput";
		}else if(nom.equals("moduleEntreeSliderInput")){
			path = "module/sliderInput";
		}else if(nom.equals("entreeSons")){
			path = "global/entree";
		}else if(nom.equals("config")){
			path = "global/configuration";
		}else if(nom.equals("master")){
			path = "tableMixage/master";
		}else if(nom.equals("masterPatch")){
			path = "tableMixage/masterModule";
		}else if(nom.equals("moduleSend")){
			path = "module/moduleSend";
		}else if(nom.equals("moduleEntreeSliderOwnS")){
			path = "module/sliderOwnS";
		}else if(nom.equals("moduleEntreeSliderS")){
			path = "module/sliderS";
		}
	
		// Copie du fichier
		if(pathModule == null){
			reference = Datas.getMainFolderPath() + "/ressources/modele/" + path + ".maxpat";
			destination = Datas.getMainFolderPath() + "/ressources/copy/" + path + ".cge." + id++ + Datas.getUniqueId() + ".maxpat";		
		}else{
			reference = formatPath(pathModule);
			destination = Datas.getMainFolderPath() + "/ressources/copy/patchesLoaded/" + "cge.patch" + id++ + Datas.getUniqueId() + ".maxpat";	
		}
		FileInputStream filesource = null;
        FileOutputStream fileDestination = null;
        try{
            filesource = new FileInputStream(reference);
            fileDestination = new FileOutputStream(destination);
            byte buffer[] = new byte[512 * 1024];
            int nblecture;
            while((nblecture = filesource.read(buffer)) != -1){
                fileDestination.write(buffer, 0, nblecture);
            }
            filesource.close();
            fileDestination.close();
        }catch(FileNotFoundException nf){
            nf.printStackTrace();
        }catch(IOException io){
            io.printStackTrace();
        }finally{
            try{
                filesource.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                fileDestination.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return destination;
        	
	}
	
	public static void saveConfig(String path){
		Datas.him.outlet(0, "clear");
		
		String toWrite = "";
		toWrite += "configBoxId:" + Datas.configBoxId;
		toWrite += " cr ";
		toWrite += "entreeExtPatcherBoxId:" + Datas.entreeExtPatcherBoxId;
		toWrite += " cr ";
		toWrite += "tableMixagePatcherBoxId:" + Datas.tableMixagePatcherBoxId;
		toWrite += " cr ";
		toWrite += "nbModules:" + Datas.getNbModules();
		toWrite += " cr ";
		toWrite += "nbModulesWithOut:" + Datas.getNbModulesWithOut();
		toWrite += " cr ";
		toWrite += "sorties:" + Datas.getSorties();
		toWrite += " cr ";
		toWrite += "entrees:" + Datas.getEntrees();
		toWrite += " cr ";
		toWrite += "cgeId:" + Datas.cgeId;
		toWrite += " cr ";
		
		//RangToId
		ArrayList<String> rangToId = Datas.rangToId;
		toWrite += "rangToId:";
		for(int i = 0 ; i < rangToId.size() ; i++){
			toWrite += (i + "/" + rangToId.get(i));
			if(i != (rangToId.size() - 1 )){			
				toWrite += "!";
			}
		}
		
		//Modules
		Hashtable<String, Module> modules = Datas.modules;
		for(int k = 0 ; k < Datas.rangToId.size() ; k++){//Module mod : modules.values()){
			Module mod = modules.get(rangToId.get(k));
			toWrite += " cr ";
			toWrite += "modules:";
			toWrite += mod.getId() + "/";
			toWrite += mod.patchCorpsId + "/";
			toWrite += mod.moduleTableMixagePatcherBoxId + "/";
			toWrite += mod.inlets + "/";
			toWrite += mod.outlets + "/";
			toWrite += mod.sizeX + "/";
			toWrite += mod.sizeY + "/";
			toWrite += mod.rang + "/";
			toWrite += mod.nom + "/";
			if(mod.getModuleEntree() != null){
				toWrite += mod.getModuleEntree().moduleEntreePatchBoxId + "/";
				toWrite += mod.getModuleEntree().moduleEntreeTabBoxId + "/";
				toWrite += mod.getModuleEntree().moduleEntreeSliderCorpsBoxId + "/";
			}else{
				toWrite += "null" + "/";
				toWrite += "null" + "/";
				toWrite += "null" + "/";
			}
			if(mod.getModuleSortie() != null){
				toWrite += mod.getModuleSortie().moduleSortiePatchBoxId + "/";
			}else{
				toWrite += "null" + "/";
			}
			toWrite += mod.getModulePatch().modulePatchId + "/";
			toWrite += mod.getModulePatch().toModuleId + "/";
			if(mod.getModuleEntree() != null){
				for(int i = 0 ; i < mod.getModuleEntree().patchsId.size() ; i++){//MpHashtable<Integer, String> ht : mod.getModuleEntree().patchsId.values()){
					Hashtable<Integer, String> ht = mod.getModuleEntree().patchsId.get(i);
					for(int j = 0 ; j < ht.size() ; j++){//String ht2 : ht.values()){
						String ht2 = ht.get(j);
						toWrite += ht2 + "!";
					}
					toWrite += "%";
				}
				toWrite += "/";
				if(Datas.getEntrees()>0){
					for(int l = 0 ; l < mod.getModuleEntree().patchsEntreeId.size() ; l++){//Hashtable<Integer, String> ht : mod.getModuleEntree().patchsEntreeId.values()){
						Hashtable<Integer, String> ht = mod.getModuleEntree().patchsEntreeId.get(l);
						for(int m = 0 ; m < ht.size() ; m++){//String ht2 : ht.values()){
							String ht2 = ht.get(m);
							toWrite += ht2 + "!";
						}
						toWrite += "%";
					}
				}else{
					toWrite += "null" + "/";
				}
			}else{
				toWrite += "null" + "/";
				toWrite += "null" + "/";
			}
		}
		Datas.him.outlet(0, toWrite);
		Datas.him.outlet(0, "write " + "\"" + path + "\"");
	}
	
	
	public static void readConfig(){

		try {
			
			BufferedReader br;
			br = new BufferedReader(new FileReader(Datas.getMainFolderPath() + "/CGEConfiguration.config"));
			String line;
			Datas.modules.clear();
			while ((line = br.readLine()) != null) {
				String propriete = line.split(":")[0];
				String arg = line.split(":")[1];
				if(propriete.equals("configBoxId")){
					Datas.configBoxId = arg;
					Datas.setConfigBox(Datas.him.getParentPatcher().getNamedBox(Datas.configBoxId));
					Datas.setConfigPatcher(Datas.getConfigBox().getSubPatcher());
				}else if(propriete.equals("entreeExtPatcherBoxId")){
					Datas.entreeExtPatcherBoxId = arg;
					Datas.setEntreeExtPatcherBox(Datas.getConfigPatcher().getNamedBox(Datas.entreeExtPatcherBoxId));
				}else if(propriete.equals("tableMixagePatcherBoxId")){
					Datas.tableMixagePatcherBoxId = arg;
					Datas.setTableMixagePatcherBox(Datas.him.getParentPatcher().getNamedBox(Datas.tableMixagePatcherBoxId));
					Datas.setMasterModule(Datas.getTableMixagePatcher().getNamedBox("cgeMasterSlider").getSubPatcher());
				}else if(propriete.equals("nbModules")){
					Datas.setNbModules(Integer.valueOf(arg));
				}else if(propriete.equals("nbModulesWithOut")){
					Datas.setNbModulesWithOut(Integer.valueOf(arg));
				}else if(propriete.equals("sorties")){
					Datas.setSorties(Integer.valueOf(arg));
				}else if(propriete.equals("entrees")){
					Datas.setEntrees(Integer.valueOf(arg));
				}else if(propriete.equals("cgeId")){
					Datas.cgeId = Integer.valueOf(arg);
				}else if(propriete.equals("rangToId")){
					Datas.rangToId.clear();
					String[] unit = arg.split("!");
					for(int i = 0 ; i < unit.length ; i++){
						String[] unit2 = unit[i].split("/");
						Datas.rangToId.add(Integer.valueOf(unit2[0]), unit2[1]);
					}
				}else if(propriete.equals("modules")){
					String[] u = arg.split("/");
					Datas.modules.put(u[0], new Module(u[0], u[1], u[2], Integer.valueOf(u[3]), Integer.valueOf(u[4]), Integer.valueOf(u[5]), Integer.valueOf(u[6]), Integer.valueOf(u[7]), u[8], u[9], u[10], u[11], u[12], u[13], u[14]));
					if(!u[10].equals("null")){
						String[] patchs = u[15].split("%");
						int i = 0;
						
						for(int j = 0 ; j < patchs.length ; j++){
							String patch = patchs[j];
							String[] patch2 = patch.split("!");
							
							for(int k = 0 ; k < patch2.length ; k++){
								String patch3 = patch2[k];
								if(patch3!=""){
									Datas.modules.get(u[0]).getModuleEntree().detectPatchs(patch3, i);
								}
							}
							i++;
						}
						if(Datas.getEntrees()>0){
							String[] patchsEntree = u[16].split("%");
							i = 0;
							for(String patchEntree : patchsEntree){
								String[] patchEntree2 = patchEntree.split("!");
								
								for(String patchEntree3 : patchEntree2){
									Datas.modules.get(u[0]).getModuleEntree().detectPatchsEntree(patchEntree3, i);
								}
								i++;
							}
							
						}
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File mainFold = new File(Datas.getMainFolderPath() + "/ressources/copy");
		deleteAllProcess(mainFold);
		
	}
	
}
