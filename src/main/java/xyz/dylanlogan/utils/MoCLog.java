package xyz.dylanlogan.utils;

import java.util.logging.Logger;

import org.apache.logging.log4j.LogManager;

import cpw.mods.fml.common.FMLLog;

public class MoCLog {

   public static final org.apache.logging.log4j.Logger logger = LogManager.getLogger("MoCreatures");

   public static void initLog() {
       logger.info("Starting LessCreatures");
   }
}
