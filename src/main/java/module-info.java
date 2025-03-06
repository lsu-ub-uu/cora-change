module se.uu.ub.cora.change {
	requires transitive se.uu.ub.cora.json;
	requires transitive se.uu.ub.cora.javaclient;
	requires java.logging;
	requires ocfl.java.core;
	requires ocfl.java.api;
	requires se.uu.ub.cora.fedoraarchive;
	requires se.uu.ub.cora.storage;

	exports se.uu.ub.cora.change;
}