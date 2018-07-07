package module;

import java.util.UUID;

import com.cycling74.max.Atom;
import com.cycling74.max.MaxBox;
import com.cycling74.max.MaxObject;
import com.cycling74.max.MaxPatcher;

import tools.Datas;
import tools.Tools;

public class ModuleSortie {

	private MaxPatcher parentPatcher;
	String id;
	public String moduleSortiePatchBoxId;
	private MaxPatcher moduleSortiePatch;
	private MaxBox moduleSortiePatchBox;
	
	public ModuleSortie(MaxPatcher parentPatcher, String moduleSortiePatchBoxId, Module parent){
		this.id = parent.getId();
		this.parentPatcher = parentPatcher;
		this.moduleSortiePatchBoxId = moduleSortiePatchBoxId;
		this.moduleSortiePatchBox = this.parentPatcher.getNamedBox(moduleSortiePatchBoxId);
		this.moduleSortiePatch = this.moduleSortiePatchBox.getSubPatcher();
	}
	
	public ModuleSortie(MaxPatcher parent, String id, int x, int y, int outlets){
		this.id = id;
		String[] pathSortie = {Tools.newPatch("moduleSortie"), "@args", id, String.valueOf(Datas.getSorties()), String.valueOf(outlets), UUID.randomUUID().toString(), "@presentation", "1"};
		moduleSortiePatchBox = parent.newDefault(0, 0, "bpatcher", Atom.newAtom(pathSortie));
		moduleSortiePatchBoxId = Datas.getCgeId();
		moduleSortiePatchBox.setName(moduleSortiePatchBoxId);
		Tools.move(moduleSortiePatchBox, x, 20, 100, y);
		moduleSortiePatch = moduleSortiePatchBox.getSubPatcher();
		Tools.midiLearnPatcher(moduleSortiePatch);
		parentPatcher = parent;
	}
	
	public MaxPatcher getPatcher(){
		return moduleSortiePatch;
	}
	
	public MaxBox getParentBox(){
		return moduleSortiePatchBox;
	}
	
}
