package dna.profiler;

import dna.graph.datastructures.DataStructure.AccessType;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.profiler.complexity.AddedComplexity;
import dna.profiler.complexity.Complexity;
import dna.profiler.complexity.ComplexityMap;

public class ProfileEntry {
	private int[][] list;

	public ProfileEntry() {
		this.list = new int[ListType.values().length][AccessType.values().length];
		for (int i = 0; i < list.length; i++) {
			for (int j = 0; j < list[i].length; j++) {
				list[i][j] = 0;
			}
		}
	}

	public int getCombined() {
		int res = 0;
		for (int i = 0; i < list.length; i++) {
			for (int j = 0; j < list[i].length; j++) {
				res += list[i][j];
			}
		}
		return res;
	}

	public boolean hasAccessesInList(ListType lt) {
		for (AccessType at : AccessType.values()) {
			if (get(lt, at) != 0)
				return true;
		}
		return false;
	}

	public int get(ListType lt, AccessType at) {
		return list[lt.ordinal()][at.ordinal()];
	}

	public void increase(ListType lt, AccessType at, int i) {
		list[lt.ordinal()][at.ordinal()] += i;
	}

	public String callsAsString(String prefix) {
		StringBuilder res = new StringBuilder();

		if (prefix.length() > 0)
			prefix += ".";

		for (ListType lt : ListType.values()) {
			for (AccessType at : AccessType.values()) {
				res.append(prefix + lt.toString() + "_" + at.toString() + "="
						+ get(lt, at) + Profiler.separator);
			}
		}

		return res.toString();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (ListType lt : ListType.values()) {
			s.append("ListType: " + lt + Profiler.separator);
			for (AccessType at : AccessType.values()) {
				s.append("  " + at.toString() + "=" + get(lt, at)
						+ Profiler.separator);
			}
		}
		return s.toString();
	}

	public ComplexityMap combinedComplexity(
			ProfilerMeasurementData.ProfilerDataType entryType,
			GraphDataStructure gds, ListType listTypeLimitor) {
		Complexity aggregated = new Complexity();
		for (ListType lt : ListType.values()) {
			if (listTypeLimitor != null && !listTypeLimitor.equals(lt))
				continue;
			for (AccessType at : AccessType.values()) {
				Complexity c = gds.getComplexityClass(lt, at, entryType);
				c.setCounter(get(lt, at));
				aggregated = new AddedComplexity(aggregated, c);
			}
		}
		return aggregated.getWeightedComplexityMap();
	}

	public ProfileEntry add(ProfileEntry other) {
		ProfileEntry res = new ProfileEntry();

		for (ListType lt : ListType.values()) {
			for (AccessType at : AccessType.values()) {
				res.increase(lt, at, this.get(lt, at));
				if (other != null)
					res.increase(lt, at, other.get(lt, at));
			}
		}

		return res;
	}
}
